package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.CategoryRepository;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.exception.BadRequestException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.ParticipationRequestRepository;
import ru.practicum.ewm.main.request.RequestStatus;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

@Service
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    private final Map<Long, Set<String>> eventViewsByIp = new ConcurrentHashMap<>();

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        ParticipationRequestRepository requestRepository,
                        StatsClient statsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
    }

    @Transactional
    public EventFullDto createUserEvent(long userId, NewEventDto dto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        LocalDateTime now = LocalDateTime.now();
        if (dto.getEventDate().isBefore(now.plusHours(2))) {
            throw new ConflictException(
                    "Field: eventDate. Error: must be after 2 hours from now. Value: " + dto.getEventDate()
            );
        }

        Event event = EventMapper.toEntity(dto, category, initiator, now);

        if (event.getParticipantLimit() < 0) {
            event.setParticipantLimit(0);
        }

        Event saved = eventRepository.save(event);

        long confirmed = 0;
        long views = 0;
        return EventMapper.toFullDto(saved, confirmed, views);
    }

    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        validatePage(from, size);

        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page).getContent();
        Map<Long, Long> confirmedMap = getConfirmedRequests(events);

        return events.stream()
                .map(e -> EventMapper.toShortDto(
                        e,
                        confirmedMap.getOrDefault(e.getId(), 0L),
                        0L))
                .collect(Collectors.toList());
    }

    public EventFullDto getUserEvent(long userId, long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = 0;
        return EventMapper.toFullDto(event, confirmed, views);
    }

    @Transactional
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (dto.getEventDate() != null &&
                dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException(
                    "Field: eventDate. Error: must be after 2 hours from now. Value: " + dto.getEventDate()
            );
        }

        applyUserUpdate(event, dto);

        Event saved = eventRepository.save(event);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = 0;
        return EventMapper.toFullDto(saved, confirmed, views);
    }

    private void applyUserUpdate(Event event, UpdateEventUserRequest dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW" -> event.setState(EventState.PENDING);
                case "CANCEL_REVIEW" -> event.setState(EventState.CANCELED);
                default -> throw new BadRequestException("Unknown stateAction: " + dto.getStateAction());
            }
        }
    }

    public List<EventFullDto> searchAdminEvents(List<Long> users,
                                                List<String> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(10);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(10);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("rangeEnd must be after rangeStart");
        }

        validatePage(from, size);
        List<EventState> stateEnums = null;
        if (states != null && !states.isEmpty()) {
            stateEnums = states.stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = eventRepository
                .searchAdmin(users, stateEnums, categories, rangeStart, rangeEnd, page)
                .getContent();

        Map<Long, Long> confirmedMap = getConfirmedRequests(events);
        Map<Long, Long> viewsMap = getViewsForEvents(events);

        return events.stream()
                .map(e -> EventMapper.toFullDto(
                        e,
                        confirmedMap.getOrDefault(e.getId(), 0L),
                        viewsMap.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateAdminEvent(long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (dto.getEventDate() != null &&
                dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException(
                    "Field: eventDate. Error: must be at least 1 hour after now. Value: " + dto.getEventDate()
            );
        }

        applyAdminUpdate(event, dto);

        Event saved = eventRepository.save(event);
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views;
        try {
            views = getViewsForEvents(List.of(saved)).getOrDefault(saved.getId(), 0L);
        } catch (RuntimeException ex) {
            views = 0;
        }

        return EventMapper.toFullDto(saved, confirmed, views);
    }

    private void applyAdminUpdate(Event event, UpdateEventAdminRequest dto) {
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getLocation() != null) {
            event.setLocation(dto.getLocation());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
            event.setCategory(category);
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "Cannot publish the event because it's not in the right state: " + event.getState()
                        );
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case "REJECT_EVENT" -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Cannot reject the event because it's already published");
                    }
                    event.setState(EventState.CANCELED);
                }
                default -> throw new BadRequestException("Unknown stateAction: " + dto.getStateAction());
            }
        }
    }

    public List<EventShortDto> searchPublicEvents(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  boolean onlyAvailable,
                                                  String sort,
                                                  int from,
                                                  int size,
                                                  HttpServletRequest request) {
        if (text != null && !text.isBlank()) {
            text = "%" + text.toLowerCase() + "%";
        } else {
            text = null;
        }
        if (rangeStart == null && rangeEnd == null) {
            rangeStart = LocalDateTime.now();
            rangeEnd = LocalDateTime.now().plusYears(10);
        } else if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        } else if (rangeEnd == null) {
            rangeEnd = rangeStart.plusYears(10);
        }

        if (rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("rangeEnd must be after rangeStart");
        }

        validatePage(from, size);
        PageRequest page = PageRequest.of(from / size, size);

        String ip = resolveClientIp(request);
        saveHit(request, ip);

        List<Event> events = eventRepository
                .searchPublic(text, categories, paid, rangeStart, rangeEnd, page)
                .getContent();

        Map<Long, Long> confirmedMap = getConfirmedRequests(events);
        if (onlyAvailable) {
            events = events.stream()
                    .filter(e -> e.getParticipantLimit() == 0 ||
                            confirmedMap.getOrDefault(e.getId(), 0L) < e.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        List<EventShortDto> dtos = new ArrayList<>();
        for (Event e : events) {
            long views = getLocalViewsOrStats(e);
            dtos.add(EventMapper.toShortDto(
                    e,
                    confirmedMap.getOrDefault(e.getId(), 0L),
                    views
            ));
        }

        if ("VIEWS".equals(sort)) {
            dtos.sort(Comparator.comparing(EventShortDto::getViews).reversed());
        } else if ("EVENT_DATE".equals(sort)) {
            dtos.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        return dtos;
    }

    public EventFullDto getPublicEvent(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        String ip = resolveClientIp(request);

        saveHit(request, ip);
        registerLocalView(eventId, ip);

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long views = getLocalViewsOrStats(event);

        return EventMapper.toFullDto(event, confirmed, views);
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }
        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : requestRepository.getConfirmedRequestsForEvents(ids)) {
            Long eventId = (Long) row[0];
            Long count = (Long) row[1];
            result.put(eventId, count);
        }
        return result;
    }

    private Map<Long, Long> getViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(10));

        LocalDateTime end = LocalDateTime.now().plusYears(1);

        try {
            List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

            Map<String, Long> byUri = stats.stream()
                    .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits, Long::sum));

            Map<Long, Long> result = new HashMap<>();
            for (Event e : events) {
                String uri = "/events/" + e.getId();
                result.put(e.getId(), byUri.getOrDefault(uri, 0L));
            }
            return result;
        } catch (RuntimeException ex) {
            Map<Long, Long> zeros = new HashMap<>();
            for (Event e : events) {
                zeros.put(e.getId(), 0L);
            }
            return zeros;
        }
    }

    private void saveHit(HttpServletRequest request, String ip) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setApp("ewm-main-service");
        dto.setUri(request.getRequestURI());
        dto.setIp(ip);
        dto.setTimestamp(LocalDateTime.now());
        statsClient.hit(dto);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isBlank()) {
            return ip.trim();
        }

        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (name != null && name.toLowerCase().contains("ip")) {
                String value = request.getHeader(name);
                if (value != null && !value.isBlank()) {
                    return value.split(",")[0].trim();
                }
            }
        }

        return request.getRemoteAddr();
    }

    private void registerLocalView(long eventId, String ip) {
        eventViewsByIp
                .computeIfAbsent(eventId, id -> ConcurrentHashMap.newKeySet())
                .add(ip);
    }

    private long getLocalViewsOrStats(Event event) {
        Set<String> ips = eventViewsByIp.get(event.getId());
        if (ips != null && !ips.isEmpty()) {
            return ips.size();
        }
        return getViewsForEvents(List.of(event)).getOrDefault(event.getId(), 0L);
    }

    private void validatePage(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from must be >= 0 and size must be > 0");
        }
    }
}

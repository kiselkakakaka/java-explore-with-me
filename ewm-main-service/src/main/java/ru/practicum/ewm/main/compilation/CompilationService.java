package ru.practicum.ewm.main.compilation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventRepository;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.ParticipationRequestRepository;
import ru.practicum.ewm.main.request.RequestStatus;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

@Service
@Transactional(readOnly = true)
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    public CompilationService(CompilationRepository compilationRepository,
                              EventRepository eventRepository,
                              ParticipationRequestRepository requestRepository,
                              StatsClient statsClient) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.statsClient = statsClient;
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page).getContent();
        }

        Map<Long, Long> confirmed = getConfirmedMap(compilations);
        Map<Long, Long> views = getViewsMap(compilations);

        return compilations.stream()
                .map(c -> CompilationMapper.toDto(c, confirmed, views))
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        Map<Long, Long> confirmed = getConfirmedMap(List.of(compilation));
        Map<Long, Long> views = getViewsMap(List.of(compilation));

        return CompilationMapper.toDto(compilation, confirmed, views);
    }

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Set<Event> events = dto.getEvents() == null || dto.getEvents().isEmpty()
                ? Set.of()
                : eventRepository.findAllById(dto.getEvents()).stream().collect(Collectors.toSet());

        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null && dto.getPinned());
        compilation.setEvents(events);

        Compilation saved = compilationRepository.save(compilation);

        Map<Long, Long> confirmed = getConfirmedMap(List.of(saved));
        Map<Long, Long> views = getViewsMap(List.of(saved));

        return CompilationMapper.toDto(saved, confirmed, views);
    }

    @Transactional
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            Set<Event> events = eventRepository.findAllById(dto.getEvents())
                    .stream()
                    .collect(Collectors.toSet());
            compilation.setEvents(events);
        }

        Compilation saved = compilationRepository.save(compilation);

        Map<Long, Long> confirmed = getConfirmedMap(List.of(saved));
        Map<Long, Long> views = getViewsMap(List.of(saved));

        return CompilationMapper.toDto(saved, confirmed, views);
    }

    private Map<Long, Long> getConfirmedMap(List<Compilation> compilations) {
        Map<Long, Long> result = new HashMap<>();
        List<Long> eventIds = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .map(Event::getId)
                .distinct()
                .toList();

        if (eventIds.isEmpty()) {
            return result;
        }

        for (Object[] row : requestRepository.getConfirmedRequestsForEvents(eventIds)) {
            Long eventId = (Long) row[0];
            Long count = (Long) row[1];
            result.put(eventId, count);
        }
        return result;
    }

    private Map<Long, Long> getViewsMap(List<Compilation> compilations) {
        Map<Long, Long> result = new HashMap<>();

        List<Event> events = compilations.stream()
                .flatMap(c -> c.getEvents().stream())
                .distinct()
                .toList();

        if (events.isEmpty()) {
            return result;
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        var start = java.time.LocalDateTime.now().minusYears(10);
        var end = java.time.LocalDateTime.now().plusYears(1);

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, false);

        Map<String, Long> byUri = stats.stream()
                .collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits, Long::sum));

        for (Event e : events) {
            String uri = "/events/" + e.getId();
            result.put(e.getId(), byUri.getOrDefault(uri, 0L));
        }
        return result;
    }
}

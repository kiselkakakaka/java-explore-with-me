package ru.practicum.ewm.main.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventRepository;
import ru.practicum.ewm.main.event.EventState;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;

@Service
@Transactional(readOnly = true)
public class RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestService(ParticipationRequestRepository requestRepository,
                          UserRepository userRepository,
                          EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        if (requestRepository.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Request already exists");
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (event.getParticipantLimit() != 0 && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("Participant limit has been reached");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        ParticipationRequest saved = requestRepository.save(request);
        return RequestMapper.toDto(saved);
    }

    public List<ParticipationRequestDto> getUserRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return RequestMapper.toDtos(requests);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            throw new ForbiddenException("Only requester can cancel the request");
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest saved = requestRepository.save(request);
        return RequestMapper.toDto(saved);
    }

    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Only event initiator can view participation requests");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        return RequestMapper.toDtos(requests);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Only event initiator can change participation requests");
        }

        if (updateRequest.getRequestIds() == null || updateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());
        }

        RequestStatus newStatus;
        try {
            newStatus = RequestStatus.valueOf(updateRequest.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ConflictException("Unknown status: " + updateRequest.getStatus());
        }

        if (newStatus != RequestStatus.CONFIRMED && newStatus != RequestStatus.REJECTED) {
            throw new ConflictException("Only CONFIRMED or REJECTED statuses are allowed");
        }

        int participantLimit = event.getParticipantLimit();
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (participantLimit != 0 && confirmed >= participantLimit && newStatus == RequestStatus.CONFIRMED) {
            throw new ConflictException("Participant limit has been reached");
        }

        List<ParticipationRequestDto> confirmedDtos = new ArrayList<>();
        List<ParticipationRequestDto> rejectedDtos = new ArrayList<>();

        for (Long requestId : updateRequest.getRequestIds()) {
            ParticipationRequest request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

            if (!Objects.equals(request.getEvent().getId(), eventId)) {
                throw new ConflictException("Request " + requestId + " does not belong to event " + eventId);
            }

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending requests can be changed");
            }

            if (newStatus == RequestStatus.CONFIRMED) {
                if (participantLimit != 0 && confirmed >= participantLimit) {
                    request.setStatus(RequestStatus.REJECTED);
                    ParticipationRequest saved = requestRepository.save(request);
                    rejectedDtos.add(RequestMapper.toDto(saved));
                    continue;
                }

                request.setStatus(RequestStatus.CONFIRMED);
                ParticipationRequest saved = requestRepository.save(request);
                confirmedDtos.add(RequestMapper.toDto(saved));
                confirmed++;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                ParticipationRequest saved = requestRepository.save(request);
                rejectedDtos.add(RequestMapper.toDto(saved));
            }
        }

        return new EventRequestStatusUpdateResult(confirmedDtos, rejectedDtos);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequests(long userId,
                                                              long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        return updateEventRequestsStatus(userId, eventId, updateRequest);
    }
}

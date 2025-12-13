package ru.practicum.ewm.main.request;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class EventRequestPrivateController {

    private final RequestService requestService;

    public EventRequestPrivateController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId,
                                                          @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable long userId,
                                                                    @PathVariable long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return requestService.updateEventRequestsStatus(userId, eventId, updateRequest);
    }
}
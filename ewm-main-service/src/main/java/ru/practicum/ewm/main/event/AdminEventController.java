package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.UpdateEventAdminRequest;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart, FORMATTER) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd, FORMATTER) : null;

        return eventService.searchAdminEvents(
                users,
                states,
                categories,
                start,
                end,
                from,
                size
        );
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest dto) {
        return eventService.updateAdminEvent(eventId, dto);
    }
}

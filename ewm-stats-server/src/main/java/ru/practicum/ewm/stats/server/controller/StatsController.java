package ru.practicum.ewm.stats.server.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody @Valid EndpointHitDto hitDto) {
        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                       LocalDateTime start,
                                       @RequestParam
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                       LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}

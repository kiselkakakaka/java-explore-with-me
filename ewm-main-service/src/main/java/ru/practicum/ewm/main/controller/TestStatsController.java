package ru.practicum.ewm.main.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

@RestController
public class TestStatsController {

    private final StatsClient statsClient;

    public TestStatsController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @GetMapping("/test")
    public String test(HttpServletRequest request) {
        EndpointHitDto dto = new EndpointHitDto();
        dto.setApp("ewm-main-service");
        dto.setUri(request.getRequestURI());
        dto.setIp(request.getRemoteAddr());
        dto.setTimestamp(LocalDateTime.now());

        statsClient.hit(dto);

        return "ok";
    }
}

package ru.practicum.ewm.stats.server.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class StatsErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    public Map<String, String> handleBadRequest(BadRequestException e) {
        return Map.of("error", e.getMessage());
    }
}


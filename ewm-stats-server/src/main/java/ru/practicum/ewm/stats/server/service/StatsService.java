package ru.practicum.ewm.stats.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StatsService {

    private final EndpointHitRepository repository;

    public StatsService(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto dto) {
        EndpointHit hit = new EndpointHit(
                null,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );

        EndpointHit saved = repository.save(hit);

        dto.setId(saved.getId());
        return dto;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end must be after start");
        }

        if (unique) {
            return repository.getStatsUnique(start, end, uris);
        } else {
            return repository.getStats(start, end, uris);
        }
    }
}
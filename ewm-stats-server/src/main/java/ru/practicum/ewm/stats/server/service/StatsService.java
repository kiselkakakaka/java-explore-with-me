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
@Transactional(readOnly = true)
public class StatsService {

    private final EndpointHitRepository repository;

    public StatsService(EndpointHitRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto dto) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(dto.getTimestamp());

        EndpointHit saved = repository.save(hit);

        EndpointHitDto result = new EndpointHitDto();
        result.setId(saved.getId());
        result.setApp(saved.getApp());
        result.setUri(saved.getUri());
        result.setIp(saved.getIp());
        result.setTimestamp(saved.getTimestamp());
        return result;
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        if (unique) {
            return repository.getStatsUnique(start, end, uris);
        } else {
            return repository.getStats(start, end, uris);
        }
    }
}

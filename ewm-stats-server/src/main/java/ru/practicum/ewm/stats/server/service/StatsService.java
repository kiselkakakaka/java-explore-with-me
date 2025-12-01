package ru.practicum.ewm.stats.server.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.model.EndpointHit;
import ru.practicum.ewm.stats.server.repository.EndpointHitRepository;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final EndpointHitRepository endpointHitRepository;

    public StatsService(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    @Transactional
    public void saveHit(EndpointHitDto dto) {
        EndpointHit hit = new EndpointHit(
                null,
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
        endpointHitRepository.save(hit);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        if (unique) {
            return endpointHitRepository.getStatsUnique(start, end, uris);
        }
        return endpointHitRepository.getStats(start, end, uris);
    }
}
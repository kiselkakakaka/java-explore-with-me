package ru.practicum.ewm.stats.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

@Component
public class StatsClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(@Value("${stats.server-url}") String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    public void hit(EndpointHitDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(dto, headers);
        restTemplate.postForEntity(serverUrl + "/hit", request, Void.class);
    }

    @SuppressWarnings("unchecked")
    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(serverUrl + "/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        ResponseEntity<List> response =
                restTemplate.getForEntity(builder.toUriString(), List.class);

        return (List<ViewStatsDto>) (List<?>) response.getBody();
    }
}
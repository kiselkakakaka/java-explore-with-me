package ru.practicum.ewm.stats.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

public class StatsClient {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public StatsClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void hit(EndpointHitDto dto) {
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(dto);
        restTemplate.exchange(baseUrl + "/hit", HttpMethod.POST, request, Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uris,
                                       boolean unique) {
        String startStr = URLEncoder.encode(start.format(FORMATTER), StandardCharsets.UTF_8);
        String endStr = URLEncoder.encode(end.format(FORMATTER), StandardCharsets.UTF_8);

        StringBuilder urlBuilder = new StringBuilder(baseUrl)
                .append("/stats?start=").append(startStr)
                .append("&end=").append(endStr)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=").append(URLEncoder.encode(uri, StandardCharsets.UTF_8));
            }
        }

        URI uri = URI.create(urlBuilder.toString());
        ResponseEntity<ViewStatsDto[]> response =
                restTemplate.getForEntity(uri, ViewStatsDto[].class);

        ViewStatsDto[] body = response.getBody();
        if (body == null || body.length == 0) {
            return List.of();
        }
        return List.of(body);
    }
}
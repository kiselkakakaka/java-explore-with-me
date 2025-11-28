package ru.practicum.ewm.stats.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StatsClientConfiguration {

    @Bean
    public RestTemplate statsRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(RestTemplate statsRestTemplate) {
        String baseUrl = "http://ewm-stats-server:9090";
        return new StatsClient(statsRestTemplate, baseUrl);
    }
}
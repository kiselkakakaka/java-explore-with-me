package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StatsClientConfiguration {

    @Value("${stats.server-url}")
    private String baseUrl;

    @Bean
    public RestTemplate statsRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient(RestTemplate statsRestTemplate) {
        return new StatsClient(statsRestTemplate, baseUrl);
    }
}
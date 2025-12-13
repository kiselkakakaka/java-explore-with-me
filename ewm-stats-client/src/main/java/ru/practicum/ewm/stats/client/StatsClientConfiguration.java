package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatsClientConfiguration {

    @Value("${stats.server-url}")
    private String baseUrl;

    @Bean
    public StatsClient statsClient() {
        return new StatsClient(baseUrl);
    }
}
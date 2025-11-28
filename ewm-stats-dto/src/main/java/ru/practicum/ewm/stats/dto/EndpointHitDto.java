package ru.practicum.ewm.stats.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EndpointHitDto {

    private Long id;

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public EndpointHitDto() {
    }

    public EndpointHitDto(Long id,
                          String app,
                          String uri,
                          String ip,
                          LocalDateTime timestamp) {
        this.id = id;
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getApp() {
        return app;
    }

    public String getUri() {
        return uri;
    }

    public String getIp() {
        return ip;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
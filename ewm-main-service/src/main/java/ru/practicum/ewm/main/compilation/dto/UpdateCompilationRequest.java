package ru.practicum.ewm.main.compilation.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateCompilationRequest {

    // null разрешаем (поле может не прийти в PATCH),
    // но запрещаем пустую строку и режем по контракту до 50
    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;
    private List<Long> events;

    public UpdateCompilationRequest() {
    }

    public String getTitle() {
        return title;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public List<Long> getEvents() {
        return events;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public void setEvents(List<Long> events) {
        this.events = events;
    }
}


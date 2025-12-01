package ru.practicum.ewm.main.compilation.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewCompilationDto {

    private List<Long> events;

    private Boolean pinned = Boolean.FALSE;

    @NotBlank
    @Size(min = 1, max = 120)
    private String title;

    public NewCompilationDto() {
    }

    public List<Long> getEvents() {
        return events;
    }

    public void setEvents(List<Long> events) {
        this.events = events;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package ru.practicum.ewm.main.compilation.dto;

import java.util.List;

import ru.practicum.ewm.main.event.dto.EventShortDto;

public class CompilationDto {

    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;

    public CompilationDto() {
    }

    public CompilationDto(Long id, String title, Boolean pinned, List<EventShortDto> events) {
        this.id = id;
        this.title = title;
        this.pinned = pinned;
        this.events = events;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public List<EventShortDto> getEvents() {
        return events;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public void setEvents(List<EventShortDto> events) {
        this.events = events;
    }
}
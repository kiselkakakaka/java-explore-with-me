package ru.practicum.ewm.main.event.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.user.dto.UserShortDto;

public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long confirmedRequests;
    private Long views;

    public EventShortDto() {
    }

    public EventShortDto(Long id,
                         String annotation,
                         CategoryDto category,
                         LocalDateTime eventDate,
                         UserShortDto initiator,
                         Boolean paid,
                         String title,
                         Long confirmedRequests,
                         Long views) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
        this.confirmedRequests = confirmedRequests;
        this.views = views;
    }

    public Long getId() {
        return id;
    }

    public String getAnnotation() {
        return annotation;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public UserShortDto getInitiator() {
        return initiator;
    }

    public Boolean getPaid() {
        return paid;
    }

    public String getTitle() {
        return title;
    }

    public Long getConfirmedRequests() {
        return confirmedRequests;
    }

    public Long getViews() {
        return views;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setInitiator(UserShortDto initiator) {
        this.initiator = initiator;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setConfirmedRequests(Long confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }

    public void setViews(Long views) {
        this.views = views;
    }
}

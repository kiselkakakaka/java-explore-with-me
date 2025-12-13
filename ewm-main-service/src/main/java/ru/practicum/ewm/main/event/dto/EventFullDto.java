package ru.practicum.ewm.main.event.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.event.EventState;
import ru.practicum.ewm.main.event.Location;
import ru.practicum.ewm.main.user.dto.UserShortDto;

public class EventFullDto {

    private Long id;
    private String annotation;
    private CategoryDto category;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long confirmedRequests;
    private Long views;

    public EventFullDto() {
    }

    public EventFullDto(Long id,
                        String annotation,
                        CategoryDto category,
                        String description,
                        LocalDateTime eventDate,
                        UserShortDto initiator,
                        Location location,
                        Boolean paid,
                        Integer participantLimit,
                        LocalDateTime createdOn,
                        LocalDateTime publishedOn,
                        Boolean requestModeration,
                        EventState state,
                        String title,
                        Long confirmedRequests,
                        Long views) {
        this.id = id;
        this.annotation = annotation;
        this.category = category;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.createdOn = createdOn;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
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

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public UserShortDto getInitiator() {
        return initiator;
    }

    public Location getLocation() {
        return location;
    }

    public Boolean getPaid() {
        return paid;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public EventState getState() {
        return state;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setInitiator(UserShortDto initiator) {
        this.initiator = initiator;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setParticipantLimit(Integer participantLimit) {
        this.participantLimit = participantLimit;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setPublishedOn(LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public void setRequestModeration(Boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public void setState(EventState state) {
        this.state = state;
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
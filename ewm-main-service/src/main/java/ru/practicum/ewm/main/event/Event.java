package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.user.User;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(nullable = false, length = 120)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Embedded
    private Location location;

    @Column(nullable = false)
    private boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private int participantLimit;

    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EventState state;

    public Event() {
    }

    public Event(Long id,
                 String annotation,
                 String description,
                 String title,
                 Category category,
                 User initiator,
                 LocalDateTime eventDate,
                 LocalDateTime createdOn,
                 LocalDateTime publishedOn,
                 Location location,
                 boolean paid,
                 int participantLimit,
                 boolean requestModeration,
                 EventState state) {
        this.id = id;
        this.annotation = annotation;
        this.description = description;
        this.title = title;
        this.category = category;
        this.initiator = initiator;
        this.eventDate = eventDate;
        this.createdOn = createdOn;
        this.publishedOn = publishedOn;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Category getCategory() {
        return category;
    }

    public User getInitiator() {
        return initiator;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isPaid() {
        return paid;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public boolean isRequestModeration() {
        return requestModeration;
    }

    public boolean getRequestModeration() {
        return requestModeration;
    }

    public EventState getState() {
        return state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setPublishedOn(LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setParticipantLimit(int participantLimit) {
        this.participantLimit = participantLimit;
    }

    public void setRequestModeration(boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public void setState(EventState state) {
        this.state = state;
    }
}
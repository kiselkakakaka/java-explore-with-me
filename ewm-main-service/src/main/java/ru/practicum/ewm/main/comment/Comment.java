package ru.practicum.ewm.main.comment;

import jakarta.persistence.*;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String text;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "edited_on")
    private LocalDateTime editedOn;

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Event getEvent() {
        return event;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getEditedOn() {
        return editedOn;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }
}


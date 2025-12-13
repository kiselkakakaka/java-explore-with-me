package ru.practicum.ewm.main.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private Long eventId;
    private Long authorId;
    private String text;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editedOn;

    public CommentDto() {
    }

    public CommentDto(Long id, Long eventId, Long authorId, String text, LocalDateTime createdOn, LocalDateTime editedOn) {
        this.id = id;
        this.eventId = eventId;
        this.authorId = authorId;
        this.text = text;
        this.createdOn = createdOn;
        this.editedOn = editedOn;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getText() {
        return text;
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

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setEditedOn(LocalDateTime editedOn) {
        this.editedOn = editedOn;
    }
}


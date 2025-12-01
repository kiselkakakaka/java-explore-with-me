package ru.practicum.ewm.main.request.dto;

import java.time.LocalDateTime;

public class ParticipationRequestDto {

    private Long id;
    private LocalDateTime created;
    private Long event;
    private Long requester;
    private String status;

    public ParticipationRequestDto() {
    }

    public ParticipationRequestDto(Long id,
                                   LocalDateTime created,
                                   Long event,
                                   Long requester,
                                   String status) {
        this.id = id;
        this.created = created;
        this.event = event;
        this.requester = requester;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public Long getEvent() {
        return event;
    }

    public Long getRequester() {
        return requester;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public void setEvent(Long event) {
        this.event = event;
    }

    public void setRequester(Long requester) {
        this.requester = requester;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
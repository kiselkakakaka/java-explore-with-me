package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.CategoryMapper;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserMapper;

public class EventMapper {

    private EventMapper() {
    }

    public static Event toEntity(NewEventDto dto,
                                 Category category,
                                 User initiator,
                                 LocalDateTime createdOn) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setTitle(dto.getTitle());
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setEventDate(dto.getEventDate());
        event.setCreatedOn(createdOn);
        event.setPublishedOn(null);
        event.setLocation(dto.getLocation());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
        event.setState(EventState.PENDING);
        return event;
    }

    public static EventFullDto toFullDto(Event event,
                                         long confirmedRequests,
                                         long views) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);
        return dto;
    }

    public static EventShortDto toShortDto(Event event,
                                           long confirmedRequests,
                                           long views) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toDto(event.getCategory()));
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(UserMapper.toShortDto(event.getInitiator()));
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);
        return dto;
    }
}
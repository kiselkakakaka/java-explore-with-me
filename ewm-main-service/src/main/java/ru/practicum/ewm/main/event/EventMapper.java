package ru.practicum.ewm.main.event;

import java.time.LocalDateTime;

import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.category.dto.CategoryDto;
import ru.practicum.ewm.main.event.dto.EventFullDto;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.dto.NewEventDto;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.dto.UserShortDto;

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
        dto.setDescription(event.getDescription());
        dto.setTitle(event.getTitle());
        dto.setEventDate(event.getEventDate());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());

        dto.setCategory(toCategoryDto(event.getCategory()));
        dto.setInitiator(toUserShortDto(event.getInitiator()));

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
        dto.setTitle(event.getTitle());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());

        dto.setCategory(toCategoryDto(event.getCategory()));
        dto.setInitiator(toUserShortDto(event.getInitiator()));

        dto.setConfirmedRequests(confirmedRequests);
        dto.setViews(views);

        return dto;
    }

    private static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    private static UserShortDto toUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }
}
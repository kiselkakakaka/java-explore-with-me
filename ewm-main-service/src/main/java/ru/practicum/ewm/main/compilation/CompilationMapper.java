package ru.practicum.ewm.main.compilation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventMapper;
import ru.practicum.ewm.main.event.dto.EventShortDto;

public class CompilationMapper {

    private CompilationMapper() {
    }

    public static CompilationDto toDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.isPinned());

        Set<Event> events = compilation.getEvents();
        List<EventShortDto> eventDtos = events.stream()
                .map(e -> EventMapper.toShortDto(e, 0L, 0L))
                .collect(Collectors.toList());
        dto.setEvents(eventDtos);
        return dto;
    }
}
package ru.practicum.ewm.main.compilation;

import java.util.List;
import java.util.Map;

import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventMapper;
import ru.practicum.ewm.main.event.dto.EventShortDto;

public final class CompilationMapper {

    private CompilationMapper() {
    }

    public static CompilationDto toDto(Compilation compilation,
                                       Map<Long, Long> confirmed,
                                       Map<Long, Long> views) {
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(e -> EventMapper.toShortDto(
                        e,
                        confirmed.getOrDefault(e.getId(), 0L),
                        views.getOrDefault(e.getId(), 0L)))
                .toList();

        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                events
        );
    }
}

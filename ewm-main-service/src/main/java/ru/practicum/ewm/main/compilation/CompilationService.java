package ru.practicum.ewm.main.compilation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventRepository;
import ru.practicum.ewm.main.exception.BadRequestException;
import ru.practicum.ewm.main.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationService(CompilationRepository compilationRepository,
                              EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        validatePage(from, size);

        PageRequest page = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(page).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }

        return compilations.stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    public CompilationDto getCompilation(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException("Compilation with id=" + compId + " was not found"));

        return CompilationMapper.toDto(compilation);
    }

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllById(dto.getEvents()));
        }

        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(Boolean.TRUE.equals(dto.getPinned()));
        compilation.setEvents(events);

        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(saved);
    }

    @Transactional
    public void deleteCompilation(long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            if (!dto.getEvents().isEmpty()) {
                events.addAll(eventRepository.findAllById(dto.getEvents()));
            }
            compilation.setEvents(events);
        }

        Compilation saved = compilationRepository.save(compilation);
        return CompilationMapper.toDto(saved);
    }

    private void validatePage(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from must be >= 0 and size must be > 0");
        }
    }
}

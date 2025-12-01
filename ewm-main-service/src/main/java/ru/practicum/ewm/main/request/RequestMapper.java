package ru.practicum.ewm.main.request;

import java.util.List;
import java.util.stream.Collectors;

import ru.practicum.ewm.main.request.dto.ParticipationRequestDto;

public class RequestMapper {

    private RequestMapper() {
    }

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        if (request == null) {
            return null;
        }

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus().name());

        if (request.getEvent() != null) {
            dto.setEvent(request.getEvent().getId());
        }
        if (request.getRequester() != null) {
            dto.setRequester(request.getRequester().getId());
        }

        return dto;
    }

    public static List<ParticipationRequestDto> toDtos(List<ParticipationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        return requests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
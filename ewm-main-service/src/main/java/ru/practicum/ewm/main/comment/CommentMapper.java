package ru.practicum.ewm.main.comment;

import ru.practicum.ewm.main.comment.dto.CommentDto;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment c) {
        Long eventId = (c.getEvent() != null) ? c.getEvent().getId() : null;
        Long authorId = (c.getAuthor() != null) ? c.getAuthor().getId() : null;

        return new CommentDto(
                c.getId(),
                eventId,
                authorId,
                c.getText(),
                c.getCreatedOn(),
                c.getEditedOn()
        );
    }
}


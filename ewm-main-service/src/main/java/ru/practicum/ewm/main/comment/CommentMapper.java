package ru.practicum.ewm.main.comment;

import ru.practicum.ewm.main.comment.dto.CommentDto;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getText(),
                comment.getCreatedOn(),
                comment.getEditedOn()
        );
    }
}

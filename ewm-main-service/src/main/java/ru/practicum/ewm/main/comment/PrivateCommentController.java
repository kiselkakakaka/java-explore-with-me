package ru.practicum.ewm.main.comment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;
import ru.practicum.ewm.main.comment.dto.UpdateCommentDto;

@RestController
@RequestMapping("/users")
public class PrivateCommentController {

    private final CommentService commentService;

    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable long userId,
                                 @PathVariable long eventId,
                                 @Valid @RequestBody NewCommentDto dto) {
        return commentService.addComment(userId, eventId, dto);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable long userId,
                                    @PathVariable long commentId,
                                    @Valid @RequestBody UpdateCommentDto dto) {
        return commentService.updateComment(userId, commentId, dto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}


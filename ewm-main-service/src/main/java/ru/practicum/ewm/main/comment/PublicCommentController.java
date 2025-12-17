package ru.practicum.ewm.main.comment;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/events")
public class PublicCommentController {

    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable long eventId) {
        return commentService.getEventComments(eventId);
    }
}


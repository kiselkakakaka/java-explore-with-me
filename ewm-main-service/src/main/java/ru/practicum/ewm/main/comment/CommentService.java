package ru.practicum.ewm.main.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.comment.dto.CommentDto;
import ru.practicum.ewm.main.comment.dto.NewCommentDto;
import ru.practicum.ewm.main.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.main.event.Event;
import ru.practicum.ewm.main.event.EventRepository;
import ru.practicum.ewm.main.event.EventState;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentDto addComment(long userId, long eventId, NewCommentDto dto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Comments are allowed only for published events");
        }

        Comment c = new Comment();
        c.setAuthor(author);
        c.setEvent(event);
        c.setText(dto.getText());
        c.setCreatedOn(LocalDateTime.now());

        Comment saved = commentRepository.save(c);
        return CommentMapper.toDto(saved);
    }

    public List<CommentDto> getEventComments(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        return commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto updateComment(long userId, long commentId, UpdateCommentDto dto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (!Objects.equals(c.getAuthor().getId(), userId)) {
            throw new ConflictException("Only author can update the comment");
        }

        c.setText(dto.getText());
        c.setEditedOn(LocalDateTime.now());

        Comment saved = commentRepository.save(c);
        return CommentMapper.toDto(saved);
    }

    @Transactional
    public void deleteComment(long userId, long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (!Objects.equals(c.getAuthor().getId(), userId)) {
            throw new ConflictException("Only author can delete the comment");
        }

        commentRepository.deleteById(commentId);
    }
}


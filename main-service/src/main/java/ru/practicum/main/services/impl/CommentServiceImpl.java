package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.exception.CommentNotExistException;
import ru.practicum.main.exception.EventNotExistException;
import ru.practicum.main.exception.RequestNotExistException;
import ru.practicum.main.exception.UserNotExistException;
import ru.practicum.main.mappers.CommentMapper;
import ru.practicum.main.models.Comment;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.Request;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.CommentRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.RequestRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.CommentService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(Long eventId, CommentDto commentDto, Long userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistException("User not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event not found"));
        Optional<Request> request = requestRepository.findRequestByRequesterIdAndEventId(userId, eventId);
        if (request.isEmpty()) {
            throw new RequestNotExistException("You did not participate in this event " + eventId);
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setEvent(event);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public CommentDto updateComment(Long comId, CommentDto comment) {
        Comment savedComment = commentRepository.findById(comId)
                .orElseThrow(() -> new CommentNotExistException("Comment not Found"));
        savedComment.setText(comment.getText());
        savedComment.setCreated(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(savedComment));
    }

    @Override
    public void deleteComment(Long userId, Long comId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("User not found with Id: " + userId);
        }
        if (!commentRepository.existsById(comId)) {
            throw new CommentNotExistException("Comment not found with Id: " + comId);
        }
        commentRepository.deleteById(comId);
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("User not found with Id: " + userId);
        }
        List<Comment> comments = commentRepository.findAllByAuthorId(userId);
        if (comments.size() == 0) {
            return Collections.emptyList();
        }
        return comments
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotExistException("Event not found with Id: " + eventId);
        }
        return commentRepository.findAllByEventId(eventId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}

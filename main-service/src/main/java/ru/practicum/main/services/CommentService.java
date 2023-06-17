package ru.practicum.main.services;

import ru.practicum.main.dto.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long eventId, CommentDto comment, Long userId);

    CommentDto updateComment(Long comId, CommentDto comment);

    void deleteComment(Long userId, Long comId);

    List<CommentDto> getAllCommentsByUser(Long userId);

    List<CommentDto> getAllCommentsByEventId(Long eventId);
}

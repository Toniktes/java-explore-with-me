package ru.practicum.main.services;

import ru.practicum.main.dto.comment.CommentDto;

public interface CommentService {
    CommentDto addComment(Long eventId, CommentDto comment, Long userId);
}

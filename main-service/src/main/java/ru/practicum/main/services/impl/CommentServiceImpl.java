package ru.practicum.main.services.impl;

import org.springframework.stereotype.Service;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.services.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public CommentDto addComment(Long eventId, CommentDto comment, Long userId) {
        return null;
    }
}

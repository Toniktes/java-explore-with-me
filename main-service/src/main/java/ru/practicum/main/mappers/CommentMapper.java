package ru.practicum.main.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.models.Comment;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.User;

@Mapper(componentModel = "spring")
@Component
public interface CommentMapper {
    Comment toComment(CommentDto commentDto);

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);

    EventShortDto toEventShortDto(Event event);

    CommentDto toDto(Comment comment);

}

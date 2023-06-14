package ru.practicum.main.dto.comment;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.user.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Valid
@Getter
@Setter
public class CommentDto {

    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 10, max = 7000)
    private String text;

    private UserDto author;

    private EventShortDto event;

    private LocalDateTime created;
}

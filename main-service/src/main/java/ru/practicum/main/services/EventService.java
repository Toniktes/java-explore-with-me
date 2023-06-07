package ru.practicum.main.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.NewEventDto;

import javax.validation.Valid;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);
}

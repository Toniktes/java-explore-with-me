package ru.practicum.main.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;

import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, Pageable pageable);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}

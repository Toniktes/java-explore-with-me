package ru.practicum.main.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.SortValue;
import ru.practicum.main.models.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, Pageable pageable);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users, EventState states, List<Long> categoriesId,
                                        String rangeStart, String rangeEnd, Pageable pageable);

    void setView(List<Event> events);

    List<EventFullDto> getEventsWithParams(String text, List<Long> categories, Boolean paid, String rangeStart,
                                           String rangeEnd, Boolean onlyAvailable, SortValue sort,
                                           Pageable pageable, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

}

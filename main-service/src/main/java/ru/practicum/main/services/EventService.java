package ru.practicum.main.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.SortValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getEventsByUser(Long userId, Pageable pageable);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> userIds, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size,
                                        HttpServletRequest request);




    List<EventShortDto> getEventsWithParams(String text, List<Long> categoriesIds, Boolean paid, String rangeStart,
                                            String rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from,
                                            Integer size, HttpServletRequest request);

    EventFullDto getEvent(Long id, HttpServletRequest request);

}

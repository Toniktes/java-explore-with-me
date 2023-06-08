package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.StateActionForUser;
import ru.practicum.main.exception.*;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.models.Category;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.CategoryRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.EventService;


import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Pageable pageable) {
        return eventMapper.toEventShortDtoList(eventRepository.findAllByInitiatorId(userId, pageable).toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotExistException("not found category"));
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException("The event cannot be earlier than two hours from the current moment");
        }
        Event event = eventMapper.toEventModel(newEventDto);
        event.setCategory(category);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistException(String.format(
                        "Can't create event, the user with id = %s doesn't exist", userId)));
        event.setInitiator(user);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException("Not found Event")));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException(String.format("Not found Event with id = %s", eventId)));

        if (event.getPublishedOn() != null) {
            throw new AlreadyPublishedException("Event already published");
        }
        if (updateEventUserRequest == null) {
            return eventMapper.toEventFullDto(event);
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new CategoryNotExistException("Not found category"));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            LocalDateTime eventDateTime = updateEventUserRequest.getEventDate();
            if (eventDateTime.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new WrongTimeException("The event cannot be earlier than two hours from the current moment");
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit().intValue());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateActionForUser.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }


}

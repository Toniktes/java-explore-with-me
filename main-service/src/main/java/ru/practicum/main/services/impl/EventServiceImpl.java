package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.exception.CategoryNotExistException;
import ru.practicum.main.exception.UserNotExistException;
import ru.practicum.main.exception.WrongTimeException;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.models.Category;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.CategoryRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.EventService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotExistException("not found category"));
        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongTimeException("the event cannot be earlier than two hours from the current moment");
        }
        Event event = eventMapper.toEventModel(newEventDto);
        event.setCategory(category);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistException(String.format(
                        "Can't create event, the user with id = %s doesn't exist", userId)));
        event.setInitiator(user);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }
}

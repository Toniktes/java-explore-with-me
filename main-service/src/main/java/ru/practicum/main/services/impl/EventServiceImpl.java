package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatClient;
import ru.practicum.main.Pattern;
import ru.practicum.main.dto.event.*;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.SortValue;
import ru.practicum.main.enums.StateActionForAdmin;
import ru.practicum.main.enums.StateActionForUser;
import ru.practicum.main.exception.*;
import ru.practicum.main.mappers.EventMapper;
import ru.practicum.main.models.Category;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.Location;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.CategoryRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.LocationRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final StatClient statClient;
    private final LocationRepository locationRepository;
    private final String datePattern = Pattern.DATE;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Pageable pageable) {
        return eventMapper.toEventShortDtoList(eventRepository.findAllByInitiatorId(userId, pageable).toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto event) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistException("Такого пользователя нет " + userId));
        validateTime(event.getEventDate());
        Event eventToSave = eventMapper.toEventModel(event);
        eventToSave.setState(EventState.PENDING);
        eventToSave.setConfirmedRequests(0);
        eventToSave.setCreatedOn(LocalDateTime.now());

        Category category = categoryRepository.findById(event.getCategory())
                .orElseThrow(() -> new CategoryNotExistException("Такой категории нет"));
        eventToSave.setCategory(category);
        eventToSave.setInitiator(user);
        Event saved = eventRepository.save(eventToSave);
        return eventMapper.toEventFullDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        return eventMapper.toEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventNotExistException("Not found Event")));
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest event) {
        Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Такого события нет " + eventId));
        if (eventFromDb.getState().equals(EventState.CANCELED) || eventFromDb.getState().equals(EventState.PENDING)) {
            if (event.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, " +
                        "чем через два часа от текущего момента ");
            }
            if (StateActionForUser.SEND_TO_REVIEW == event.getStateAction()) {
                eventFromDb.setState(EventState.PENDING);
            }
            if (StateActionForUser.CANCEL_REVIEW == event.getStateAction()) {
                eventFromDb.setState(EventState.CANCELED);
            }
        } else {
            throw new AlreadyPublishedException("Изменить можно только отмененные события или события в состоянии ожидания модерации, " +
                    "статус события = " + eventFromDb.getState());
        }

        updateEventEntity(event, eventFromDb);
        eventRepository.save(eventFromDb);
        return eventMapper.toEventFullDto(eventFromDb);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest event) {
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Такого события нет " + eventId));
        if (event.getEventDate() != null) {
            validateTime(event.getEventDate());
        }
        if (event.getStateAction() != null) {
            if (event.getStateAction() == StateActionForAdmin.PUBLISH_EVENT) {
                if (eventToUpdate.getState().equals(EventState.PENDING)) {
                    eventToUpdate.setState(EventState.PUBLISHED);
                    eventToUpdate.setPublishedOn(LocalDateTime.now());
                } else {
                    throw new AlreadyPublishedException("Событие можно публиковать, только если оно в состоянии ожидания публикации" +
                            event.getStateAction());
                }
            }
            if (event.getStateAction() == StateActionForAdmin.REJECT_EVENT) {
                if (eventToUpdate.getState().equals(EventState.PUBLISHED)) {
                    throw new AlreadyPublishedException("Событие можно отклонить, только если оно еще не опубликовано " +
                            event.getStateAction());
                }
                eventToUpdate.setState(EventState.CANCELED);
            }
        }
        updateEventEntity(event, eventToUpdate);

        eventRepository.save(eventToUpdate);
        return eventMapper.toEventFullDto(eventToUpdate);
    }

    private void validateTime(LocalDateTime start) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала события должна быть не ранее чем за час от даты публикации");
        }
    }

    private void updateEventEntity(UpdateEventAdminRequest event, Event eventToUpdate) {
        eventToUpdate.setAnnotation(Objects.requireNonNullElse(event.getAnnotation(), eventToUpdate.getAnnotation()));
        eventToUpdate.setCategory(event.getCategory() == null
                ? eventToUpdate.getCategory()
                : categoryRepository.findById(event.getCategory()).orElseThrow(() -> new CategoryNotExistException("Category not fount")));
        eventToUpdate.setDescription(Objects.requireNonNullElse(event.getDescription(), eventToUpdate.getDescription()));
        eventToUpdate.setEventDate(Objects.requireNonNullElse(event.getEventDate(), eventToUpdate.getEventDate()));
        eventToUpdate.setLocation(event.getLocation() == null
                ? eventToUpdate.getLocation()
                : locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon())
                .orElse(new Location(null, event.getLocation().getLat(), event.getLocation().getLon())));
        eventToUpdate.setPaid(Objects.requireNonNullElse(event.getPaid(), eventToUpdate.getPaid()));
        eventToUpdate.setParticipantLimit(Objects.requireNonNullElse(event.getParticipantLimit(), eventToUpdate.getParticipantLimit()));
        eventToUpdate.setRequestModeration(Objects.requireNonNullElse(event.getRequestModeration(), eventToUpdate.getRequestModeration()));
        eventToUpdate.setTitle(Objects.requireNonNullElse(event.getTitle(), eventToUpdate.getTitle()));
    }

    private void updateEventEntity(UpdateEventUserRequest event, Event eventToUpdate) {
        eventToUpdate.setAnnotation(Objects.requireNonNullElse(event.getAnnotation(), eventToUpdate.getAnnotation()));
        eventToUpdate.setCategory(event.getCategory() == null
                ? eventToUpdate.getCategory()
                : categoryRepository.findById(event.getCategory()).orElseThrow(() -> new CategoryNotExistException("Category not fount")));
        eventToUpdate.setDescription(Objects.requireNonNullElse(event.getDescription(), eventToUpdate.getDescription()));
        eventToUpdate.setEventDate(Objects.requireNonNullElse(event.getEventDate(), eventToUpdate.getEventDate()));
        eventToUpdate.setLocation(event.getLocation() == null
                ? eventToUpdate.getLocation()
                : locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon())
                .orElse(new Location(null, event.getLocation().getLat(), event.getLocation().getLon())));
        eventToUpdate.setPaid(Objects.requireNonNullElse(event.getPaid(), eventToUpdate.getPaid()));
        eventToUpdate.setParticipantLimit(Objects.requireNonNullElse(event.getParticipantLimit(), eventToUpdate.getParticipantLimit()));
        eventToUpdate.setRequestModeration(Objects.requireNonNullElse(event.getRequestModeration(), eventToUpdate.getRequestModeration()));
        eventToUpdate.setTitle(Objects.requireNonNullElse(event.getTitle(), eventToUpdate.getTitle()));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> userIds, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size,
                                               HttpServletRequest request) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        if (states == null & rangeStart == null & rangeEnd == null) {
            return eventRepository.findAll(pageRequest)
                    .stream()
                    .map(eventMapper::toEventFullDto)
                    .collect(Collectors.toList());
        }

        List<EventState> stateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());

        LocalDateTime start;
        if (rangeStart != null && !rangeStart.isEmpty()) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
        } else {
            start = LocalDateTime.now().plusYears(5);
        }

        LocalDateTime end;
        if (rangeEnd != null && !rangeEnd.isEmpty()) {
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
        } else {
            end = LocalDateTime.now().plusYears(5);
        }

        if (userIds.size() != 0 && states.size() != 0 && categories.size() != 0) {
            return findEventDtosWithAllParameters(userIds, categories, pageRequest, stateList, start, end);
        }
        if (userIds.size() == 0 && categories.size() != 0) {
            return findEventDtosWithAllParameters(userIds, categories, pageRequest, stateList, start, end);
        } else {
            return new ArrayList<>();
        }
    }

    private List<EventFullDto> findEventDtosWithAllParameters(List<Long> userIds, List<Long> categories,
                                                              PageRequest pageRequest, List<EventState> stateList,
                                                              LocalDateTime start, LocalDateTime end) {
        Page<Event> eventsWithPage = eventRepository.findAllWithAllParameters(userIds, stateList, categories, start, end,
                pageRequest);
        Set<Long> eventIds = eventsWithPage.stream().map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> viewStatsMap = statClient.getSetViewsByEventId(eventIds);

        List<EventFullDto> events = eventsWithPage.stream().map(eventMapper::toEventFullDto).collect(Collectors.toList());
        events.forEach(eventFullDto ->
                eventFullDto.setViews(viewStatsMap.getOrDefault(eventFullDto.getId(), 0L)));
        return events;
    }

    @Override
    public List<EventShortDto> getEventsWithParams(String text, List<Long> categoriesIds, Boolean paid, String rangeStart,
                                                   String rangeEnd, Boolean onlyAvailable, SortValue sort, Integer from,
                                                   Integer size, HttpServletRequest request) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, dateFormatter);
            end = LocalDateTime.parse(rangeEnd, dateFormatter);
            if (start.isAfter(end)) {
                throw new ValidationException("Wrong dates");
            }
        } else {
            if (rangeStart == null && rangeEnd == null) {
                start = LocalDateTime.now();
                end = LocalDateTime.now().plusYears(10);
            } else {
                if (rangeStart == null) {
                    start = LocalDateTime.now();
                }
                if (rangeEnd == null) {
                    end = LocalDateTime.now();
                }
            }
        }

        final PageRequest pageRequest = PageRequest.of(from / size, size,
                Sort.by(SortValue.EVENT_DATE.equals(sort) ? "eventDate" : "views"));
        List<Event> eventEntities = eventRepository.searchPublishedEvents(categoriesIds, paid, start, end, pageRequest)
                .getContent();
        statClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        if (eventEntities.isEmpty()) {
            return Collections.emptyList();
        }
        java.util.function.Predicate<Event> eventEntityPredicate;
        if (text != null && !text.isEmpty()) {
            eventEntityPredicate = eventEntity -> eventEntity.getAnnotation().toLowerCase().contains(text.toLowerCase())
                    || eventEntity.getDescription().toLowerCase().contains(text.toLowerCase());
        } else {
            eventEntityPredicate = eventEntity -> true;
        }

        Set<Long> eventIds = eventEntities.stream().filter(eventEntityPredicate).map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> viewStatsMap = statClient.getSetViewsByEventId(eventIds);

        List<EventShortDto> events = eventEntities.stream().map(eventMapper::toEventShortDto).collect(Collectors.toList());
        events.forEach(eventShortDto ->
                eventShortDto.setViews(viewStatsMap.getOrDefault(eventShortDto.getId(), 0L)));
        return events;
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotExistException("Такого события нет " + eventId));

        statClient.saveHit("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        Long views = statClient.getStatisticsByEventId(eventId);

        EventFullDto eventDto = eventMapper.toEventFullDto(event);
        eventDto.setViews(views);

        return eventDto;
    }

}

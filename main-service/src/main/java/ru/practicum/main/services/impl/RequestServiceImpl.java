package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.RequestStatusToUpdate;
import ru.practicum.main.exception.*;
import ru.practicum.main.mappers.RequestMapper;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.Request;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.RequestRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getCurrentUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("Такого пользователя нет");
        }
        List<Request> requests = requestRepository.findAllByRequesterIdInForeignEvents(userId);
        return requests.stream().map(requestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new UserNotExistException("Такого пользователя нет "
                + userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("Такого события нет "
                + eventId));
        Request request = new Request(LocalDateTime.now(), event, requester, RequestStatus.PENDING);
        Optional<Request> requests = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requests.isPresent()) {
            throw new AlreadyExistsException("Нельзя добавить повторный запрос: userId {}, eventId {} " + userId + eventId);
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new AlreadyExistsException("Инициатор события не может добавить запрос на участие в своём событии " + userId);
        }
        if (!(event.getState().equals(EventState.PUBLISHED))) {
            throw new AlreadyExistsException("Нельзя участвовать в неопубликованном событии");
        }
        int limit = event.getParticipantLimit();
        if (limit != 0) {
            if (limit == event.getConfirmedRequests()) {
                throw new AlreadyExistsException("У события достигнут лимит запросов на участие: " + limit);
            }
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequests(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new RequestNotExistException(String.format("Request with id=%s was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserOfEvent(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("Такого пользователя нет");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotExistException("Такого события нет");
        }
        List<Request> requests = requestRepository.findAllUserRequestsInEvent(userId, eventId);
        return requests.stream().map(requestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest eventRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException("Такого пользователя нет");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotExistException("Такого события нет "
                + eventId));
        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            throw new AlreadyExistsException("Подтверждение заявки не требуется " + eventId);
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new AlreadyExistsException("Превышен лимит подтвержденных заявок " + eventId);
        }

        List<Long> requestIds = eventRequest.getRequestIds();

        RequestStatusToUpdate status = eventRequest.getStatus();

        List<Request> requests = requestIds.stream().map((id) -> requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotExistException("Такой заявки нет "
                        + id))).collect(Collectors.toList());

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        List<Request> updatedRequests = new ArrayList<>();

        for (Request req : requests) {
            if (status == RequestStatusToUpdate.CONFIRMED && req.getStatus().equals(RequestStatus.PENDING)) {
                if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    req.setStatus(RequestStatus.REJECTED);
                    updatedRequests.add(req);
                    rejectedRequests.add(req);
                }
                req.setStatus(RequestStatus.CONFIRMED);
                updatedRequests.add(req);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(req);
            }
            if (status == RequestStatusToUpdate.REJECTED && req.getStatus().equals(RequestStatus.PENDING)) {
                req.setStatus(RequestStatus.REJECTED);
                updatedRequests.add(req);
                rejectedRequests.add(req);
            }
        }

        requestRepository.saveAll(updatedRequests);
        eventRepository.save(event);

        List<ParticipationRequestDto> con = confirmedRequests.stream().map(requestMapper::toRequestDto).collect(Collectors.toList());
        List<ParticipationRequestDto> rej = rejectedRequests.stream().map(requestMapper::toRequestDto).collect(Collectors.toList());

        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        updateResult.setRejectedRequests(rej);
        updateResult.setConfirmedRequests(con);

        return updateResult;
    }
}

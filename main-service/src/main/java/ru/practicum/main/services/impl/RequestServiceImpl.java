package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.enums.RequestStatus;
import ru.practicum.main.enums.RequestStatusToUpdate;
import ru.practicum.main.exception.*;
import ru.practicum.main.mappers.RequestMapper;
import ru.practicum.main.models.Event;
import ru.practicum.main.models.Request;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.repositories.RequestRepository;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.RequestService;

import java.time.LocalDateTime;
import java.util.List;
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
            throw new UserNotExistException(String.format("User with id=%s was not found", userId));
        }
        return requestMapper.toRequestDtoList(requestRepository.findAllByRequester(userId));
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new RequestAlreadyExistException("Request already exists");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event doesnt exist"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new WrongUserException("Can't create request by initiator");
        }

        if (event.getPublishedOn() == null) {
            throw new EventIsNotPublishedException("Event is not published");
        }

        if (!event.getRequestModeration() &&
                requestRepository.findAllByEvent(eventId).size() >= event.getParticipantLimit()) {
            throw new ExceedingLimitException("The limit of applications for participation has been exceeded");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventId);
        request.setRequester(userId);
        request.setStatus(RequestStatus.PENDING);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequests(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequester(requestId, userId)
                .orElseThrow(() -> new RequestNotExistException(String.format("Request with id=%s was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsByUserOfEvent(Long userId, Long eventId) {
        return requestMapper.toRequestDtoList(requestRepository.findAllByEventAndRequester(eventId, userId));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest eventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotExistException("Event doesn't exist"));
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        List<Request> requests = requestRepository.findAllByEventAndRequester(eventId, userId);
        List<Request> requestsToUpdate = requests
                .stream()
                .filter(x -> eventRequest.getRequestIds().contains(x.getId()))
                .collect(Collectors.toList());

        if (requestsToUpdate
                .stream()
                .anyMatch(x -> x.getStatus().equals(RequestStatus.CONFIRMED) &&
                        eventRequest.getStatus().equals(RequestStatusToUpdate.REJECTED))) {
            throw new RequestAlreadyConfirmedException("request already confirmed");
        }

        if (event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit() &&
                eventRequest.getStatus().equals(RequestStatusToUpdate.CONFIRMED)) {
            throw new ExceedingLimitException("exceeding the limit of participants");
        }

        for (Request x : requestsToUpdate) {
            x.setStatus(RequestStatus.valueOf(eventRequest.getStatus().toString()));
        }

        requestRepository.saveAll(requestsToUpdate);

        switch (eventRequest.getStatus()) {
            case CONFIRMED:
                event.setConfirmedRequests(event.getConfirmedRequests() + requestsToUpdate.size());
                eventRepository.save(event);
                result.setConfirmedRequests(requestMapper.toRequestDtoList(requestsToUpdate));
                break;
            case REJECTED:
                result.setRejectedRequests(requestMapper.toRequestDtoList(requestsToUpdate));
        }
        return result;
    }
}

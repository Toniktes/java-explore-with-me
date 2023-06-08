package ru.practicum.main.controllers.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getCurrentUserRequests(@PathVariable(name = "userId") Long userId) {
        log.debug("received a request to getCurrentUserRequests");
        return requestService.getCurrentUserRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto addRequest(@PathVariable(name = "userId") Long userId, @RequestParam(name = "eventId") Long eventId) {
        log.debug("received a request to addRequest");
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") Long userId, @PathVariable Long requestId) {
        log.debug("received a request to cancelRequest");
        return requestService.cancelRequests(userId, requestId);
    }
}

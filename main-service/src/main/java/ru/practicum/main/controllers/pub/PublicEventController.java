package ru.practicum.main.controllers.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.Pattern;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.enums.SortValue;
import ru.practicum.main.services.CommentService;
import ru.practicum.main.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping
    public List<EventShortDto> getEventsByPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = Pattern.DATE) String rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = Pattern.DATE) String rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) SortValue sort,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        return eventService.getEventsWithParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }

    @GetMapping("/comments/all/{userId}")
    public List<CommentDto> getAllUserComments(@PathVariable Long userId) {
        log.debug("received a request to getAllUserComments with userId: {} ", userId);
        return commentService.getAllCommentsByUser(userId);
    }

    @GetMapping("/{eventId}/comments/all")
    public List<CommentDto> getAllEventComments(@PathVariable Long eventId) {
        log.debug("received a request to getAllEventComments with eventId: {} ", eventId);
        return commentService.getAllCommentsByEventId(eventId);
    }
}

package ru.practicum.main.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.services.CommentService;
import ru.practicum.main.services.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final CommentService commentService;

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "eventId") Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.updateEvent(eventId, updateEventAdminRequest);

    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsByAdmin(@RequestParam(name = "users", required = false) List<Long> userIds,
                                               @RequestParam(name = "states", required = false) List<String> states,
                                               @RequestParam(name = "categories", required = false) List<Long> categories,
                                               @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                               @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        return eventService.getEventsByAdmin(userIds, states, categories, rangeStart, rangeEnd, from, size, request);
    }

    @PatchMapping("/comments/{comId}")
    public CommentDto updateComment(@PathVariable Long comId, @Valid @RequestBody CommentDto comment) {
        log.debug("received a request to updateComment {} ", comId);
        return commentService.updateComment(comId, comment);
    }

    @DeleteMapping("comments/{userId}/{comId}")
    public void deleteComment(@PathVariable Long userId, @PathVariable Long comId) {
        log.debug("received a request to deleteComment userId: {}, commentId: {} ", userId, comId);
        commentService.deleteComment(userId, comId);
    }
}

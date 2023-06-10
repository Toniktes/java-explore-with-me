package ru.practicum.hit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.hit.dto.EndpointHitDto;
import ru.practicum.hit.service.HitService;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {

    private final HitService service;

    @PostMapping(value = "/hit")
    public ResponseEntity<EndpointHitDto> addHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.debug("received a request to add Hit with body={}", endpointHitDto);
        return ResponseEntity.ok().body(service.addHit(endpointHitDto));
    }

    @GetMapping(value = "/stats")
    public ResponseEntity<List<ViewStatsDto>> getStat(@RequestParam String start,
                                                      @RequestParam String end,
                                                      @RequestParam(required = false) List<String> uris,
                                                      @RequestParam(defaultValue = "false") String unique) {
        log.debug("received a request to getStat with endpoint /stats");
        return ResponseEntity.ok().body(service.getStat(start, end, uris, unique));
    }
}
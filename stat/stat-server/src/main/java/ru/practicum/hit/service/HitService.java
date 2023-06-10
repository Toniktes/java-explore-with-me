package ru.practicum.hit.service;

import ru.practicum.hit.dto.EndpointHitDto;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.util.List;

public interface HitService {

    EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStat(String start, String end, List<String> uris, String unique);
}

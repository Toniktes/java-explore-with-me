package ru.practicum.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.hit.dto.EndpointHitDto;
import ru.practicum.hit.mapper.HitMapper;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.hit.repository.HitRepository;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceIml implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Override
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = hitMapper.toEndpointHit(endpointHitDto);

        return hitMapper.toEndpointHitDto(hitRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getStat(String start, String end, List<String> uris, String unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);
        boolean onlyUnique = Boolean.parseBoolean(unique);
        List<ViewStatsDto> viewStats;
        if (onlyUnique) {
            if (uris != null) {
                viewStats = hitRepository.getUniqueWithUris(timeStart, timeEnd, uris);
            } else {
                viewStats = hitRepository.getUniqueWithOutUris(timeStart, timeEnd);
            }
        } else {
            if (uris != null) {
                viewStats = hitRepository.getWithUris(timeStart, timeEnd, uris);
            } else {
                viewStats = hitRepository.getWithOutUris(timeStart, timeEnd);
            }
        }
        return viewStats;
    }
}

package ru.practicum.hit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ValidationException;
import ru.practicum.hit.dto.EndpointHitDto;
import ru.practicum.hit.mapper.HitMapper;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.hit.repository.HitRepository;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.hit.Constants.dateTimeFormatter;

@Slf4j
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
        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), dateTimeFormatter);
            endDate = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), dateTimeFormatter);
        } catch (Exception e) {
            throw new ValidationException("Wrong date format");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Wrong start and end dates");
        }
        boolean onlyUnique = Boolean.parseBoolean(unique);
        if (onlyUnique) {
            if (uris != null && !uris.isEmpty()) {
                uris.replaceAll(s -> s.replace("[", ""));
                uris.replaceAll(s -> s.replace("]", ""));
                return hitRepository.getUniqueWithUris(startDate, endDate, uris);
            } else {
                return hitRepository.getUniqueWithOutUris(startDate, endDate);
            }
        } else {
            if (uris != null && !uris.isEmpty()) {
                uris.replaceAll(s -> s.replace("[", ""));
                uris.replaceAll(s -> s.replace("]", ""));
                return hitRepository.getWithUris(startDate, endDate, uris);
            } else {
                return hitRepository.getWithOutUris(startDate, endDate);
            }
        }
    }
}

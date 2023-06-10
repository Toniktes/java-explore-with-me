package ru.practicum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.hit.dto.Hit;
import ru.practicum.viewStatsDto.ViewStatsDto;

import static ru.practicum.client.Pattern.date;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class StatClient extends BaseClient {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<List<ViewStatsDto>> mapType = new TypeReference<>() {
    };

    @Autowired
    public StatClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(String app,
                                          String uri,
                                          String ip,
                                          LocalDateTime timestamp) {
        Hit endpointHitDto = new Hit(app, uri, ip, timestamp);
        return post("/hit", endpointHitDto);
    }

    public Long getStatisticsByEventId(Long eventId) {
        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.now().minusYears(1000).format(date),
                "end", LocalDateTime.now().plusYears(1000).format(date),
                "uris", List.of("/events/" + eventId),
                "unique", Boolean.TRUE
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);

        List<ViewStatsDto> viewStatsList = response.hasBody() ? mapper.convertValue(response.getBody(), mapType) : Collections.emptyList();
        return viewStatsList != null && !viewStatsList.isEmpty() ? viewStatsList.get(0).getHits() : 0L;
    }

    public Map<Long, Long> getSetViewsByEventId(Set<Long> eventIds) {
        Map<String, Object> parameters = Map.of(
                "start", LocalDateTime.now().minusYears(1000).format(date),
                "end", LocalDateTime.now().plusYears(1000).format(date),
                "uris", (eventIds.stream().map(id -> "/events/" + id).collect(Collectors.toList())),
                "unique", Boolean.FALSE
        );
        ResponseEntity<Object> response = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);

        return response.hasBody() ? mapper.convertValue(response.getBody(), mapType)
                .stream()
                .collect(Collectors.toMap(this::getEventIdFromURI, ViewStatsDto::getHits))
                : Collections.emptyMap();
    }

    private Long getEventIdFromURI(ViewStatsDto e) {
        return Long.parseLong(e.getUri().substring(e.getUri().lastIndexOf("/") + 1));
    }

}
package ru.practicum.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT new ru.practicum.viewStatsDto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE (h.timestamp between :start AND :end) AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<ViewStatsDto> getUniqueWithUris(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd,
                                         @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.viewStatsDto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp between :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<ViewStatsDto> getUniqueWithOutUris(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd);

    @Query(value = "SELECT new ru.practicum.viewStatsDto.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE (h.timestamp between :start AND :end) AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<ViewStatsDto> getWithUris(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd,
                                   @Param("uris") List<String> uris);

    @Query(value = "SELECT new ru.practicum.viewStatsDto.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp between :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<ViewStatsDto> getWithOutUris(@Param("start") LocalDateTime timeStart, @Param("end") LocalDateTime timeEnd);
}

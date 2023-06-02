package ru.practicum.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.EndpointHit;
import ru.practicum.viewStatsDto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT h.app, h.uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM hits AS h " +
            "WHERE h.created_date >= :timeStart AND h.created_date <= :timeEnd AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ", nativeQuery = true)
    List<ViewStatsDto> getUniqueWithUris(LocalDateTime timeStart, LocalDateTime timeEnd, List<String> uris);

    @Query(value = "SELECT h.app, h.uri, COUNT(DISTINCT h.ip) AS hits " +
            "FROM hits AS h " +
            "WHERE h.created_date >= :timeStart AND h.created_date <= :timeEnd " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ", nativeQuery = true)
    List<ViewStatsDto> getUniqueWithOutUris(LocalDateTime timeStart, LocalDateTime timeEnd);

    @Query(value = "SELECT h.app, h.uri, COUNT(h.ip) AS hits " +
            "FROM hits AS h " +
            "WHERE h.created_date >= :timeStart AND h.created_date <= :timeEnd AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ", nativeQuery = true)
    List<ViewStatsDto> getWithUris(LocalDateTime timeStart, LocalDateTime timeEnd, List<String> uris);

    @Query(value = "SELECT h.app, h.uri, COUNT(h.ip) AS hits " +
            "FROM hits AS h " +
            "WHERE h.created_date >= :timeStart AND h.created_date <= :timeEnd " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ", nativeQuery = true)
    List<ViewStatsDto> getWithOutUris(LocalDateTime timeStart, LocalDateTime timeEnd);

}

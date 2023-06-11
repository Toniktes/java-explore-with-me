package ru.practicum.main.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.main.enums.EventState;
import ru.practicum.main.models.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Set<Event> findAllByIdIn(Set<Long> events);

    Optional<Event> findByIdAndPublishedOnIsNotNull(Long eventId);

    Optional<Event> findByIdAndState(Long eventId, EventState eventStatus);

    @Query("SELECT e FROM Event e " +
            "JOIN e.category " +
            "WHERE ((:categoryIds) is null or e.category.id IN (:categoryIds)) " +
            "AND e.state = 'PUBLISHED' " +
            "AND (:paid is null or paid is :paid) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    Page<Event> searchPublishedEvents(@Param("categoryIds") List<Long> categoryIds,
                                            @Param("paid") Boolean paid,
                                            @Param("rangeStart") LocalDateTime start,
                                            @Param("rangeEnd") LocalDateTime end,
                                            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "JOIN e.initiator " +
            "JOIN e.category " +
            "WHERE e.initiator.id IN :userIds " +
            "AND e.state IN :states " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    Page<Event> findAllWithAllParameters(@Param("userIds") List<Long> userIds,
                                               @Param("states") List<EventState> states,
                                               @Param("categories") List<Long> categories,
                                               @Param("rangeStart") LocalDateTime rangeStart,
                                               @Param("rangeEnd") LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.id = :id " +
            "AND e.state ='PUBLISHED'")
    Optional<Event> findEventByIdAndStatePublished(@Param("id") Long eventId);
}

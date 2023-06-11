package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.models.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterAndEvent(Long userId, Long eventId);

    List<Request> findAllByEvent(Long eventId);

    List<Request> findAllByRequester(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findAllByEventAndRequester(Long eventId, Long userId);

    Optional<Request> findByRequesterAndEvent(Long userId, Long eventId);

    @Query("SELECT p FROM Request AS p " +
            "JOIN FETCH p.event e " +
            "WHERE p.event = :eventId AND e.initiator.id = :userId")
    List<Request> findAllByEventWithInitiator(@Param(value = "userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT p FROM Request p " +
            "JOIN FETCH p.event e " +
            "WHERE e.initiator.id =:userId " +
            "AND e.id = :eventId")
    List<Request> findAllUserRequestsInEvent(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT p FROM Request p " +
            "WHERE p.requester.id = :userId " +
            "AND p.event.id = :eventId")
    Optional<Request> findByRequesterIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query("SELECT p FROM Request p " +
            "JOIN FETCH p.event e " +
            "WHERE p.requester.id =:userId " +
            "AND e.initiator.id <> :userId")
    List<Request> findAllByRequesterIdInForeignEvents(@Param("userId") Long userId);
}

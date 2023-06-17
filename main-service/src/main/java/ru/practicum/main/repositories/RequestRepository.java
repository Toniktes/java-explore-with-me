package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.models.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

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

    @Query("SELECT p FROM Request p " +
            "JOIN FETCH p.event e " +
            "WHERE p.requester.id =:requesterId " +
            "AND e.id = :eventId " +
            "AND p.status = 'CONFIRMED'")
    Optional<Request> findRequestByRequesterIdAndEventId(@Param("requesterId") Long requesterId,
                                                         @Param("eventId") Long eventId);
}

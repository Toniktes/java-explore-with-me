package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.models.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterAndEvent(Long userId, Long eventId);

    List<Request> findAllByEvent(Long eventId);

    List<Request> findAllByRequester(Long userId);

    Optional<Request> findByIdAndRequester(Long requestId, Long userId);

    List<Request> findAllByEventAndRequester(Long eventId, Long userId);

    @Query("SELECT p FROM Request AS p " +
            "JOIN Event AS e ON p.event = e.id " +
            "WHERE p.event = :eventId AND e.initiator.id = :userId")
    List<Request> findAllByEventWithInitiator(@Param(value = "userId") Long userId, @Param("eventId") Long eventId);
}

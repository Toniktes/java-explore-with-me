package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.models.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByRequesterAndEvent(Long userId, Long eventId);

    List<Request> findAllByEvent(Long eventId);

    List<Request> findAllByRequester(Long userId);

    Optional<Request> findByIdAndRequester(Long requestId, Long userId);

    List<Request> findAllByEventAndRequester(Long eventId, Long userId);
}

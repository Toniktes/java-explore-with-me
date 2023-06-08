package ru.practicum.main.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.models.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long userId, Pageable pageable);
    Boolean existsByCategoryId(Long categoryId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);
}

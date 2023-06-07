package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.models.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Boolean existsByCategoryId(Long categoryId);
}

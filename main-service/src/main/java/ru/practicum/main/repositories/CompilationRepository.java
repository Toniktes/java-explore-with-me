package ru.practicum.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.models.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}

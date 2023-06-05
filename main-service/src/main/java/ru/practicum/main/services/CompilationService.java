package ru.practicum.main.services;

import ru.practicum.main.dto.compilation.CompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}

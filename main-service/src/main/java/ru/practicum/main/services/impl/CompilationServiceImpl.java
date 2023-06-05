package ru.practicum.main.services.impl;

import org.springframework.stereotype.Service;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.services.CompilationService;

import java.util.List;

@Service
public class CompilationServiceImpl implements CompilationService {
    @Override
    public CompilationDto getCompilation(Long compId) {
        return null;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }
}

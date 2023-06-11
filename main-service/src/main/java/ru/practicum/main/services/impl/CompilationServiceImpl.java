package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main.exception.CompilationNotExistException;
import ru.practicum.main.mappers.CompilationMapper;
import ru.practicum.main.models.Compilation;
import ru.practicum.main.models.Event;
import ru.practicum.main.repositories.CompilationRepository;
import ru.practicum.main.repositories.EventRepository;
import ru.practicum.main.services.CompilationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;


    @Override
    public CompilationDto addCompilation(NewCompilationDto compilation) {
        if (compilation.getEvents() != null && compilation.getEvents().size() != 0) {
            Set<Long> eventIds = compilation.getEvents();
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventIds));
            Compilation compilationEntity = compilationMapper.toCompilation(compilation);
            compilationEntity.setEvents(events);
            Compilation savedCompil = compilationRepository.save(compilationEntity);
            return compilationMapper.mapToCompilationDto(savedCompil);
        }
        Compilation fromDto = compilationMapper.toCompilation(compilation);
        if (fromDto.getEvents() == null) {
            fromDto.setEvents(new HashSet<>());
        }
        Compilation savedCompil = compilationRepository.save(fromDto);
        return compilationMapper.mapToCompilationDto(savedCompil);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilation) {
        Compilation compilationFromDb = compilationRepository.findById(compId).orElseThrow(() ->
                new CompilationNotExistException("The compilation doesn't exist"));
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            Set<Long> eventIds = compilation.getEvents();
            Set<Event> events = new HashSet<>(eventRepository.findAllByIdIn(eventIds));
            compilationFromDb.setEvents(events);
        }
        if (compilation.getPinned() != null) {
            compilationFromDb.setPinned(compilation.getPinned());
        }
        if (compilation.getTitle() != null) {
            compilationFromDb.setTitle(compilation.getTitle());
        }
        Compilation updated = compilationRepository.save(compilationFromDb);
        return compilationMapper.mapToCompilationDto(updated);
    }


/*    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotExistException("Compilation doesn't exist"));
        return compilationMapper.mapToCompilationDto(compilation);
    }*/

    /*  @Override
      public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
          CriteriaBuilder builder = entityManager.getCriteriaBuilder();
          CriteriaQuery<Compilation> query = builder.createQuery(Compilation.class);

          Root<Compilation> root = query.from(Compilation.class);
          Predicate criteria = builder.conjunction();

          if (pinned != null) {
              Predicate isPinned;
              if (pinned) {
                  isPinned = builder.isTrue(root.get("pinned"));
              } else {
                  isPinned = builder.isFalse(root.get("pinned"));
              }
              criteria = builder.and(criteria, isPinned);
          }

          query.select(root).where(criteria);
          List<Compilation> compilations = entityManager.createQuery(query)
                  .setFirstResult(pageable.getPageNumber())
                  .setMaxResults(pageable.getPageSize())
                  .getResultList();
          return compilationMapper.mapToListCompilationDto(compilations);
      }*/
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compilationEntities = compilationRepository.findAllByPinned(pinned, PageRequest.of(from / size, size, Sort.by("id")));
        return compilationEntities.stream().map(compilationMapper::mapToCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotExistException("Такой подборки нет " + id));
        return compilationMapper.mapToCompilationDto(compilation);
    }
}

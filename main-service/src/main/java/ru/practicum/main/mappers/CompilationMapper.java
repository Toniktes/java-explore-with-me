package ru.practicum.main.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.models.Compilation;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto mapToCompilationDto(Compilation compilation);

    List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations);

    default Compilation toCompilation(NewCompilationDto dto) {
        Compilation entity = new Compilation();
        entity.setPinned(dto.getPinned() != null && dto.getPinned());
        entity.setTitle(dto.getTitle());
        return entity;
    }



}

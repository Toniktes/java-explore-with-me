package ru.practicum.main.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.compilation.CompilationDto;
import ru.practicum.main.dto.compilation.NewCompilationDto;
import ru.practicum.main.models.Compilation;

@Component
@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto mapToCompilationDto(Compilation compilation);

    default Compilation toCompilation(NewCompilationDto dto) {
        Compilation entity = new Compilation();
        entity.setPinned(dto.getPinned() != null && dto.getPinned());
        entity.setTitle(dto.getTitle());
        return entity;
    }



}

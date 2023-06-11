package ru.practicum.main.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.models.Request;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface RequestMapper {
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    ParticipationRequestDto toRequestDto(Request request);
}

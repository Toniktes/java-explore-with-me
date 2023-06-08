package ru.practicum.main.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.models.Request;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface RequestMapper {
    ParticipationRequestDto toRequestDto(Request request);

    List<ParticipationRequestDto> toRequestDtoList(List<Request> requests);
}

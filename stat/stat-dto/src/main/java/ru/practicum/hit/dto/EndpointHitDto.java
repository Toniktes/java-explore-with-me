package ru.practicum.hit.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
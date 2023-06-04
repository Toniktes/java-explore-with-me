package ru.practicum.viewStatsDto;

import lombok.*;

@Getter
@Setter
public class ViewStatsDto {
    private String app;
    private String uri;
    private long hits;

    public ViewStatsDto(String app, String uri, long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}

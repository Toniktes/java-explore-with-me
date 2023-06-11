package ru.practicum.main.dto.compilation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}

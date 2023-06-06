package ru.practicum.main.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @Size(min = 2, max = 250)
    private String name;
    @Size(min = 2, max = 254)
    private String email;
}

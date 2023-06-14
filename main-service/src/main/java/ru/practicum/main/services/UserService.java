package ru.practicum.main.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto addUser(UserDto userDto);

    void deleteUser(Long userId);
}

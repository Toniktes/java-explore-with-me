package ru.practicum.main.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.services.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false, name = "ids") List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.debug("received a request to get all Users");
        return userService.getUsers(ids, PageRequest.of(from, size));
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("received a request to add User");
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.debug("received a request to deleting User id: {}", userId);
        userService.deleteUser(userId);
    }

}

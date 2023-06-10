package ru.practicum.main.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.NameAlreadyExistException;
import ru.practicum.main.exception.ValidationException;
import ru.practicum.main.mappers.UserMapper;
import ru.practicum.main.models.User;
import ru.practicum.main.repositories.UserRepository;
import ru.practicum.main.services.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        //validate(userDto);
        if (userRepository.existsByName(userDto.getName())) {
            throw new NameAlreadyExistException(String.format("Can't create user with name: %s, the name was used by another user",
                    userDto.getName()));
        }
        User user = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new ValidationException("Email can't be empty and must contains @");
        }
        if (userRepository.existsByName(userDto.getName())) {
            throw new NameAlreadyExistException(String.format("Can't create user with name: %s, the name was used by another user",
                    userDto.getName()));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        return ids == null ? userMapper.toUserDtoList(userRepository.findAll(pageable).getContent()) :
                userMapper.toUserDtoList(userRepository.findAllByIdIn(ids, pageable).getContent());
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}

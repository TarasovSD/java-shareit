package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long id);

    Optional<UserDto> updateUser(UserDto userDto, Long id);

    void removeUserById(Long id);
}

package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserDto> saveUser(User user);

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserById(Long id);

    Optional<UserDto> updateUser(User user, Long id);

    void removeUserById(Long id);
}

package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserEmailIsNullException;
import ru.practicum.shareit.exceptions.UserIsNullException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.Map.Entry;

@Repository
@Slf4j
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long generatorId = 1L;

    @Override
    public Optional<UserDto> saveUser(User user) {
        validateUser(user);
        validateEmail(user);
        user.setId(generatorId);
        users.put(generatorId, user);
        generatorId++;
        UserDto userDto = UserMapper.toUserDto(user);
        return Optional.of(userDto);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> listOfUserDto = new ArrayList<>();
        for (Entry<Long, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user != null) {
                UserDto userDto = UserMapper.toUserDto(user);
                listOfUserDto.add(userDto);
            } else {
                throw new UserNotFoundException("Пользователь не найден");
            }
        }
        return listOfUserDto;
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        User user = null;
        for (Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getKey(), id)) {
                user = entry.getValue();
            }
        }
        if (user != null) {
            return Optional.of(UserMapper.toUserDto(user));
        } else {
            log.info("Пользователь с id {} не найден!", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> updateUser(User user, Long id) {
        User foundUser = UserMapper.toUser(getUserById(id).get());
        if (user.getName() != null) {
            foundUser.setName(user.getName());
        }
        validateEmail(user);
        if (user.getEmail() != null && !user.getEmail().equals(foundUser.getEmail())) {
            foundUser.setEmail(user.getEmail());
        }
        users.put(id, foundUser);
        return Optional.of(UserMapper.toUserDto(foundUser));
    }

    @Override
    public void removeUserById(Long id) {
        users.remove(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserIsNullException("Пользователь = null");
        }
        if (user.getEmail() == null) {
            throw new UserEmailIsNullException("Email пользователя = null");
        }
    }

    private void validateEmail(User user) {
        for (Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), user.getEmail())) {
                throw new UserEmailAlreadyExistsException("Пользователь с таким email уже есть в базе");
            }
        }
    }
}

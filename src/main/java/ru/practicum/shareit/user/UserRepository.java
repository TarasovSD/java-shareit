package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserEmailIsNullException;
import ru.practicum.shareit.exceptions.UserIsNullException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepository {

    private Map<Long, User> users = new HashMap<>();
    private Long generatorId = 1L;

    public Optional<UserDto> saveUser(User user) {
        if (user == null) {
            throw new UserIsNullException("Пользователь = null");
        }
        if (user.getEmail() == null) {
            throw new UserEmailIsNullException("Email пользователя = null");
        }
        for (Entry<Long, User> entry : users.entrySet()) {
            if (Objects.equals(entry.getValue().getEmail(), user.getEmail())) {
                throw new UserEmailAlreadyExistsException("Пользователь с таким email уже есть в базе");
            }
        }
        user.setId(generatorId);
        users.put(generatorId, user);
        generatorId++;
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        return Optional.of(userDto);
    }
}

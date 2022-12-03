package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Создание нового пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@Validated(Create.class) @PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с id {}", id);
        return userClient.updateUserById(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Запрос пользователя по ID: " + id);
        return userClient.getUserById(id);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос всех пользователей");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeUserById(@PathVariable Long id) {
        log.info("Запрос на удаление юзера с ID:" + id);
        return userClient.removeUserById(id);
    }
}

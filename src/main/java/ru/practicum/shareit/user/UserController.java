package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    protected final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Пользователь создан. Кол-во пользователей:" + userService.getAllUsers().size());
        return userService.createUser(userDto);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("Выполнен запрос findAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable Long id) {
        log.info("Выполнен запрос getUserById по ID: " + id);
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public Optional<UserDto> updateUserById(@Validated(Create.class) @PathVariable Long id, @RequestBody UserDto userDto) {
        Optional<UserDto> optionalUserDto = userService.updateUser(userDto, id);
        log.info("Пользователь с id {} обновлен", id);
        return optionalUserDto;
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable Long id) {
        log.info("Выполнен запрос removeUserById для ID:" + id);
        userService.removeUserById(id);
    }
}

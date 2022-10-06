package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserNotFoundException;
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
    public Optional<UserDto> createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        Optional<UserDto> optionalUserDto = userService.createUser(userDto);
        log.info("Пользователь создан. Кол-во пользователей:" + userService.getAllUsers().size());
        return optionalUserDto;
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("Выполнен запрос findAllUsers");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<UserDto> getUserById(@PathVariable Long id) {
        log.info("Выполнен запрос getUserById по ID: " + id);
        Optional<UserDto> optionalUser = userService.getUserById(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return optionalUser;
    }

    @PatchMapping("/{id}")
    public Optional<UserDto> updateUserById(@Validated(Create.class) @PathVariable Long id, @RequestBody UserDto userDto) {
        Optional<UserDto> optionalUserDto = userService.updateUser(userDto, id);
        optionalUserDto.orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        log.info("Пользователь с id {} обновлен", id);
        return optionalUserDto;
    }

    @DeleteMapping("/{id}")
    public void removeUserById(@PathVariable Long id) {
        log.info("Выполнен запрос removeUserById для ID:" + id);
        userService.removeUserById(id);
    }
}

package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    protected UserServiceImpl userService;

    @PostMapping()
    public Optional<UserDto> createUser(@Valid @RequestBody UserDto userDto) {

        Optional<UserDto> optionalUserDto = userService.createUser(userDto);
//        log.info("Создана запись по пользователю. Кол-во записей:" + userService.getAllUsers().size());

        return optionalUserDto;
    }
}

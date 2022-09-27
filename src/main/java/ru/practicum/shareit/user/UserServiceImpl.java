package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

@Service
public class UserServiceImpl {

    @Autowired
    UserRepository userRepository;

    public Optional<UserDto> createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userRepository.saveUser(user);
    }
}

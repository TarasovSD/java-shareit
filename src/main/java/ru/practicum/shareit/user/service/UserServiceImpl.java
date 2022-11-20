package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserEmailIsNullException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> listOfUserDto = new ArrayList<>();
        for (User user : users) {
            if (user != null) {
                UserDto userDto = UserMapper.toUserDto(user);
                listOfUserDto.add(userDto);
            }
        }
        return listOfUserDto;
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            User foundUser = userRepository.findById(id).get();
            UserDto foundUserDto = UserMapper.toUserDto(foundUser);
            return Optional.of(foundUserDto);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    @Transactional
    public Optional<UserDto> updateUser(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        if (getUserById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        User foundUser = UserMapper.toUser(getUserById(id).get());
        if (user.getName() != null) {
            foundUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            foundUser.setEmail(user.getEmail());
        }
        return Optional.of(UserMapper.toUserDto(userRepository.save(foundUser)));
    }

    @Override
    @Transactional
    public void removeUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.delete(userRepository.findById(id).get());
    }

    private void validateUser(User user) {
        if (user.getEmail() == null) {
            throw new UserEmailIsNullException("Email пользователя = null");
        }
    }
}

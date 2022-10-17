package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserEmailIsNullException;
import ru.practicum.shareit.exceptions.UserIsNullException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepositoryInMemory userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        return UserMapper.toUserDto(userRepository.saveUser(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
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
        User foundUser = userRepository.getUserById(id);
        if (foundUser != null) {
            UserDto foundUserDto = UserMapper.toUserDto(foundUser);
            return Optional.of(foundUserDto);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Optional<UserDto> updateUser(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        User foundUser = UserMapper.toUser(getUserById(id).get());
        if (user.getName() != null) {
            foundUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().equals(foundUser.getEmail())) {
            foundUser.setEmail(user.getEmail());
        }
        Optional<UserDto> updatedUser = Optional.of(UserMapper.toUserDto(userRepository.updateUser(foundUser, id)));
        updatedUser.orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        return updatedUser;
    }

    @Override
    public void removeUserById(Long id) {
        userRepository.removeUserById(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserIsNullException("Пользователь = null");
        }
        if (user.getEmail() == null) {
            throw new UserEmailIsNullException("Email пользователя = null");
        }
    }
}

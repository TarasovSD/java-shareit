package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;

    public UserServiceImpl(UserRepositoryInMemory userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserDto> createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return userRepository.saveUser(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public Optional<UserDto> updateUser(UserDto userDto, Long id) {
        User user = UserMapper.toUser(userDto);
        return userRepository.updateUser(user, id);
    }

    @Override
    public void removeUserById(Long id) {
        userRepository.removeUserById(id);
    }
}

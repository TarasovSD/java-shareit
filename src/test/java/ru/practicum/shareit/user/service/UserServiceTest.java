package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.UserEmailIsNullException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    private UserRepository userRepository;

    private User user1;

    private User updatedUser;

    private UserDto userDto1;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user1 = new User(1L, "user 1", "user1@ya.ru");
        userDto1 = UserMapper.toUserDto(user1);
        updatedUser = new User(1L, "updated user 1", "updatedUser1@ya.ru");
    }

    @Test
    void createUser() {
        when(userRepository.save(user1))
                .thenReturn(user1);

        final UserDto userDto1 = userService.createUser(UserMapper.toUserDto(user1));

        assertNotNull(userDto1);
        assertEquals(1L, userDto1.getId());
        assertEquals("user 1", userDto1.getName());
        assertEquals("user1@ya.ru", userDto1.getEmail());

        assertThrows(UserEmailIsNullException.class, () -> userService.createUser(new UserDto(null, "name", null)));

        verify(userRepository, times(1))
                .save(user1);
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1));

        final List<UserDto> userDtos = userService.getAllUsers();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        assertEquals(1L, userDtos.get(0).getId());
        assertEquals("user 1", userDtos.get(0).getName());
        assertEquals("user1@ya.ru", userDtos.get(0).getEmail());

        verify(userRepository, times(1))
                .findAll();
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        Optional<UserDto> foundUserDto = userService.getUserById(1L);

        assertNotNull(foundUserDto.get());
        assertEquals(1L, foundUserDto.get().getId());
        assertEquals("user 1", foundUserDto.get().getName());
        assertEquals("user1@ya.ru", foundUserDto.get().getEmail());

        verify(userRepository, times(2))
                .findById(user1.getId());
    }

    @Test
    void updateUser() {
        when(userRepository.save(user1))
                .thenReturn(updatedUser);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        Optional<UserDto> updatedUserDto = userService.updateUser(userDto1, user1.getId());

        assertNotNull(updatedUserDto.get());
        assertEquals(1L, updatedUserDto.get().getId());
        assertEquals("updated user 1", updatedUserDto.get().getName());
        assertEquals("updatedUser1@ya.ru", updatedUserDto.get().getEmail());

        verify(userRepository, times(4))
                .findById(user1.getId());
        verify(userRepository, times(1))
                .save(user1);

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userDto1, user1.getId()));
    }

    @Test
    void removeUserById() {
        when(userRepository.save(user1))
                .thenReturn(user1);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        userService.createUser(UserMapper.toUserDto(user1));

        verify(userRepository, times(1))
                .save(user1);

        userService.removeUserById(user1.getId());

        verify(userRepository, times(2))
                .findById(user1.getId());

        verify(userRepository, times(1))
                .delete(user1);

        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user1.getId()));

    }
}
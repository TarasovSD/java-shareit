package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    private User user1;

    private User updatedUser1;
    UserDto userDto1;

    UserDto updatedUserDto1;

    private User user2;

    UserDto userDto2;

    @Captor
    ArgumentCaptor<UserDto> captor;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "user 1", "user1@ya.ru");
        userDto1 = UserMapper.toUserDto(user1);
        user2 = new User(2L, "user 2", "user2@ya.ru");
        userDto2 = UserMapper.toUserDto(user2);
        updatedUser1 = new User(1L, "updatedUser 1", "updatedUser1@ya.ru");
        updatedUserDto1 = UserMapper.toUserDto(updatedUser1);
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(userDto1))
                .thenReturn(userDto1);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(userService, times(1))
                .createUser(userDto1);

        verify(userService, times(1))
                .createUser(captor.capture());
        final var arg = captor.getValue();
        assertEquals(userDto1, arg);
    }

    @Test
    void findAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto1, userDto2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"user 1\",\"email\":\"user1@ya.ru\"},{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"}]"));

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(user1.getId()))
                .thenReturn(Optional.ofNullable(userDto1));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"user 1\",\"email\":\"user1@ya.ru\"}"));

        verify(userService, times(1))
                .getUserById(user1.getId());
    }

    @Test
    void updateUserById() throws Exception {
        when(userService.updateUser(updatedUserDto1, user1.getId()))
                .thenReturn(Optional.of(updatedUserDto1));

        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updatedUserDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto1.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto1.getEmail())));

        verify(userService, times(1))
                .updateUser(updatedUserDto1, user1.getId());
    }

    @Test
    void removeUserById() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService requestService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<ItemRequestDto> captor;

    private User user1;
    private ItemRequest request1;

    private ItemRequestDto itemRequestDto1;

    private ItemRequestDtoWithResponses itemRequestDtoWithResponses1;


    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user 1", "user1@ya.ru");
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        request1 = new ItemRequest(1L, "request description", 1L, created);
        Item item1 = new Item(1L, "item", "item description", true, 1L, request1.getId());
        ItemRequestDtoWithResponses.ItemDtoForRequest itemDto1 = ItemMapper.toItemDtoForRequest(item1);
        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(request1);
        itemRequestDtoWithResponses1 = ItemRequestMapper.toItemRequestDtoWithResponses(request1, List.of(itemDto1));

    }

    @Test
    void createRequest() throws Exception {
        when(requestService.createRequest(itemRequestDto1, user1.getId()))
                .thenReturn(itemRequestDto1);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto1.getCreated().toString())));

        verify(requestService, times(1))
                .createRequest(itemRequestDto1, user1.getId());

        verify(requestService, times(1))
                .createRequest(captor.capture(), any());
        final var arg = captor.getValue();
        assertEquals(itemRequestDto1, arg);
    }

    @Test
    void getListOfOwnRequests() throws Exception {
        when(requestService.getListOfOwnRequests(user1.getId()))
                .thenReturn(List.of(itemRequestDtoWithResponses1));

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"description\":\"request description\"," +
                        "\"created\":\"2022-11-14T23:24:30\",\"items\":[{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"requestId\":1}]}]"));

        verify(requestService, times(1))
                .getListOfOwnRequests(user1.getId());
    }

    @Test
    void getListOfAllRequests() throws Exception {
        when(requestService.getListOfAllRequests(PageRequest.of(0, 15), user1.getId()))
                .thenReturn(List.of(itemRequestDtoWithResponses1));

        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"description\":\"request description\"," +
                        "\"created\":\"2022-11-14T23:24:30\",\"items\":[{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"requestId\":1}]}]"));

        verify(requestService, times(1))
                .getListOfAllRequests(PageRequest.of(0, 15), user1.getId());
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(request1.getId(), user1.getId()))
                .thenReturn(Optional.of(itemRequestDtoWithResponses1));

        mockMvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoWithResponses1.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoWithResponses1.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDtoWithResponses1.getCreated().toString())))
                .andExpect(content().json("{\"id\":1,\"description\":\"request description\"," +
                        "\"created\":\"2022-11-14T23:24:30\",\"items\":[{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"requestId\":1}]}"));

        verify(requestService, times(1))
                .getRequestById(request1.getId(), user1.getId());
    }
}
package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<ItemDto> captor;

    private User user1;

    private Item item1;

    private Item updatedItem1;

    private ItemDto updatedItemDto1;

    private ItemDto itemDto1;

    private ShortItemDto shortItemDto1;

    private ItemInfoDto itemInfoDto1;

    private ItemInfoDto.CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user 1", "user1@ya.ru");
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        ItemRequest request1 = new ItemRequest(1L, "request description", 1L, created);
        item1 = new Item(1L, "item", "item description", true, 1L, request1.getId());
        itemDto1 = ItemMapper.toItemDto(item1);
        shortItemDto1 = ItemMapper.toShortItemDto(item1);
        updatedItem1 = new Item(1L, "item updated", "item description updated", false,
                1L, request1.getId());
        updatedItemDto1 = ItemMapper.toItemDto(updatedItem1);
        LocalDateTime startLastBooking = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endLastBooking = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        Booking lastBooking = new Booking(1L, startLastBooking, endLastBooking, item1, user1, Status.APPROVED);
        LocalDateTime startNextBooking = LocalDateTime.of(2027, 1, 1, 0, 0, 0);
        LocalDateTime endNextBooking = LocalDateTime.of(2028, 1, 1, 0, 0, 0);
        Booking nextBooking = new Booking(2L, startNextBooking, endNextBooking, item1, user1, Status.WAITING);
        Comment comment = new Comment(1L, "text", item1, user1,
                LocalDateTime.of(2020, 1, 1, 0, 0));
        commentDto = CommentMapper.toCommentDto(comment);
        List<ItemInfoDto.CommentDto> listOfComments = new ArrayList<>();
        listOfComments.add(CommentMapper.toCommentDto(comment));
        itemInfoDto1 = ItemMapper.toItemInfoDto(item1, lastBooking, nextBooking, listOfComments);

    }

    @Test
    void createItem() throws Exception {
        when(itemService.createItem(itemDto1, user1.getId()))
                .thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(itemService, times(1))
                .createItem(itemDto1, user1.getId());

        verify(itemService, times(1))
                .createItem(captor.capture(), any());
        final var arg = captor.getValue();
        assertEquals(itemDto1, arg);
    }

    @Test
    void updateItemById() throws Exception {
        when(itemService.updateItem(updatedItemDto1, updatedItem1.getId(), user1.getId()))
                .thenReturn(updatedItemDto1);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(updatedItemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto1.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(updatedItemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(updatedItemDto1.getRequestId()), Long.class));

        verify(itemService, times(1))
                .updateItem(updatedItemDto1, updatedItem1.getId(), user1.getId());
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(item1.getId(), user1.getId()))
                .thenReturn(Optional.ofNullable(itemInfoDto1));

        mockMvc.perform(get("/items/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfoDto1.getAvailable())))
                .andExpect(jsonPath("$.request", is(itemInfoDto1.getRequest().intValue())));

        verify(itemService, times(1))
                .getItemById(item1.getId(), user1.getId());
    }

    @Test
    void getItemsListByUserId() throws Exception {
        when(itemService.getItemsListByUserId(user1.getId(), PageRequest.of(0, 15)))
                .thenReturn(List.of(itemInfoDto1));

        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"request\":1," +
                        "\"lastBooking\":{\"id\":1,\"bookerId\":1,\"start\":\"2025-01-01T00:00:00\"," +
                        "\"end\":\"2026-01-01T00:00:00\"},\"nextBooking\":{\"id\":2,\"bookerId\":1," +
                        "\"start\":\"2027-01-01T00:00:00\",\"end\":\"2028-01-01T00:00:00\"},\"comments\":[{\"id\":1," +
                        "\"text\":\"text\",\"authorName\":\"user 1\",\"created\":\"2020-01-01T00:00:00\"}]}]"));

        verify(itemService, times(1))
                .getItemsListByUserId(user1.getId(), PageRequest.of(0, 15));
    }

    @Test
    void getItemsListBySearch() throws Exception {
        when(itemService.getItemsListBySearch("item", PageRequest.of(0, 15)))
                .thenReturn(List.of(shortItemDto1));

        mockMvc.perform(get("/items/search?text=item")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true}]"));

        verify(itemService, times(1))
                .getItemsListBySearch("item", PageRequest.of(0, 15));
    }

    @Test
    void createComment() throws Exception {
        when(itemService.createComment(commentDto, user1.getId(), item1.getId()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(itemService, times(1))
                .createComment(commentDto, user1.getId(), item1.getId());
    }
}
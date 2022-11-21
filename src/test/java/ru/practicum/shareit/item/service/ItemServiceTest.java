package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.CommentCreateException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository itemRepository;

    private ItemService itemService;

    private UserRepository userRepository;

    private BookingRepository bookingRepository;

    private CommentRepository commentRepository;

    private User user1;

    private Item item1;

    private Item item2;

    private ItemDto itemToUpdateDto;
    private ItemDto itemDto1;

    private Booking lastBooking;

    private Booking nextBooking;

    private Comment comment;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
        user1 = new User(1L, "user 1", "user1@ya.ru");
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        ItemRequest request1 = new ItemRequest(1L, "request description", 1L, created);
        item1 = new Item(1L, "item 1", "description item 1", true, user1.getId(), request1.getId());
        item2 = new Item(2L, "item 2", "description item 2", true, 2L, request1.getId());
        itemDto1 = ItemMapper.toItemDto(item1);
        Item itemToUpdate = new Item(1L, "updated item 1", "updated description item 1", false, user1.getId(), request1.getId());
        itemToUpdateDto = ItemMapper.toItemDto(itemToUpdate);
        LocalDateTime startLastBooking = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endLastBooking = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        lastBooking = new Booking(1L, startLastBooking, endLastBooking, item1, user1, Status.APPROVED);
        LocalDateTime startNextBooking = LocalDateTime.of(2027, 1, 1, 0, 0, 0);
        LocalDateTime endNextBooking = LocalDateTime.of(2028, 1, 1, 0, 0, 0);
        nextBooking = new Booking(2L, startNextBooking, endNextBooking, item1, user1, Status.WAITING);
        comment = new Comment(1L, "text", item1, user1,
                LocalDateTime.of(2020, 1, 1, 0, 0));
    }

    @Test
    void createItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.save(item1))
                .thenReturn(item1);

        final ItemDto itemDtoForTest = itemService.createItem(itemDto1, user1.getId());

        assertNotNull(itemDtoForTest);
        assertEquals(1L, itemDtoForTest.getId());
        assertEquals("item 1", itemDtoForTest.getName());
        assertEquals("description item 1", itemDtoForTest.getDescription());
        assertEquals(true, itemDtoForTest.getAvailable());
        assertEquals(1L, itemDtoForTest.getRequestId());

        verify(userRepository, times(1))
                .findById(user1.getId());
        verify(itemRepository, times(1))
                .save(item1);

        assertThrows(UserNotFoundException.class, () -> itemService.createItem(new ItemDto(item1.getId(),
                item1.getName(), item1.getDescription(), item1.getAvailable(), item1.getRequestId()), 12L));
    }

    @Test
    void updateItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.save(item1))
                .thenReturn(item1);
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));

        final ItemDto updatedItemDto = itemService.updateItem(itemToUpdateDto, item1.getId(), user1.getId());

        assertNotNull(updatedItemDto);
        assertEquals(1L, updatedItemDto.getId());
        assertEquals("updated item 1", updatedItemDto.getName());
        assertEquals("updated description item 1", updatedItemDto.getDescription());
        assertEquals(false, updatedItemDto.getAvailable());
        assertEquals(1L, updatedItemDto.getRequestId());

        verify(userRepository, times(1))
                .findById(user1.getId());
        verify(itemRepository, times(1))
                .save(item1);
        verify(itemRepository, times(1))
                .findById(item1.getId());

        assertThrows(UserNotFoundException.class, () -> itemService.updateItem(itemToUpdateDto, item1.getId(), 2L));
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(itemToUpdateDto, 2L, 1L));
        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(ItemMapper.toItemDto(item2),
                2L, 1L));


    }

    @Test
    void getItemById() {
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.getLastByItemId(any(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.getNextByItemId(any(), any()))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findAll())
                .thenReturn(List.of(comment));

        Optional<ItemInfoDto> itemInfoDtoOptional = itemService.getItemById(item1.getId(), user1.getId());
        ItemInfoDto itemInfoDto = itemInfoDtoOptional.get();

        assertNotNull(itemInfoDto);
        assertEquals(1L, itemInfoDto.getId());
        assertEquals("item 1", itemInfoDto.getName());
        assertEquals("description item 1", itemInfoDto.getDescription());
        assertEquals(true, itemInfoDto.getAvailable());
        assertEquals(1L, itemInfoDto.getRequest());
        assertEquals(new ItemInfoDto.ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId(),
                lastBooking.getStart(), lastBooking.getEnd()), itemInfoDto.getLastBooking());
        assertEquals(new ItemInfoDto.ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId(),
                nextBooking.getStart(), nextBooking.getEnd()), itemInfoDto.getNextBooking());
        assertEquals(List.of(new ItemInfoDto.CommentDto(1L, "text", user1.getName(),
                LocalDateTime.of(2020, 1, 1, 0, 0))), itemInfoDto.getComments());

        verify(itemRepository, times(1))
                .findById(item1.getId());
        verify(bookingRepository, times(1))
                .getLastByItemId(any(), any());
        verify(bookingRepository, times(1))
                .getNextByItemId(any(), any());
        verify(commentRepository, times(1))
                .findAll();
    }

    @Test
    void getItemsListByUserId() {
        when(itemRepository.findAllByOwnerId(user1.getId(), PageRequest.of(0, 15)))
                .thenReturn(List.of(item1));
        when(bookingRepository.getLastByItemId(any(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.getNextByItemId(any(), any()))
                .thenReturn(List.of(nextBooking));
        when(commentRepository.findAll())
                .thenReturn(List.of(comment));

        List<ItemInfoDto> itemInfoDtoList = itemService.getItemsListByUserId(user1.getId(), PageRequest.of(0, 15));

        assertNotNull(itemInfoDtoList.get(0));
        assertEquals(1L, itemInfoDtoList.get(0).getId());
        assertEquals("item 1", itemInfoDtoList.get(0).getName());
        assertEquals("description item 1", itemInfoDtoList.get(0).getDescription());
        assertEquals(true, itemInfoDtoList.get(0).getAvailable());
        assertEquals(1L, itemInfoDtoList.get(0).getRequest());
        assertEquals(new ItemInfoDto.ItemBookingDto(lastBooking.getId(), lastBooking.getBooker().getId(),
                lastBooking.getStart(), lastBooking.getEnd()), itemInfoDtoList.get(0).getLastBooking());
        assertEquals(new ItemInfoDto.ItemBookingDto(nextBooking.getId(), nextBooking.getBooker().getId(),
                nextBooking.getStart(), nextBooking.getEnd()), itemInfoDtoList.get(0).getNextBooking());
        assertEquals(List.of(new ItemInfoDto.CommentDto(1L, "text", user1.getName(),
                LocalDateTime.of(2020, 1, 1, 0, 0))), itemInfoDtoList.get(0).getComments());

        verify(itemRepository, times(1))
                .findAllByOwnerId(user1.getId(), PageRequest.of(0, 15));
        verify(bookingRepository, times(1))
                .getLastByItemId(any(), any());
        verify(bookingRepository, times(1))
                .getNextByItemId(any(), any());
        verify(commentRepository, times(1))
                .findAll();
    }

    @Test
    void getItemsListBySearch() {
        when(itemRepository.getAll(PageRequest.of(0, 15)))
                .thenReturn(List.of(item1));

        List<ShortItemDto> shortItemDtoList = itemService.getItemsListBySearch("item 1", PageRequest.of(0, 15));

        assertNotNull(shortItemDtoList.get(0));
        assertEquals(1, shortItemDtoList.size());
        assertEquals(ItemMapper.toShortItemDto(item1), shortItemDtoList.get(0));

        verify(itemRepository, times(1))
                .getAll(PageRequest.of(0, 15));
    }

    @Test
    void createComment() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.getLastBookings(any(), any(), any()))
                .thenReturn(List.of(lastBooking));
        when(commentRepository.save(any()))
                .thenReturn(comment);

        ItemInfoDto.CommentDto commentDto = itemService.createComment(new ItemInfoDto.CommentDto(1L, "text",
                        user1.getName(), LocalDateTime.of(2020, 1, 1, 0, 0)),
                user1.getId(), item1.getId());

        assertNotNull(commentDto);
        assertEquals(CommentMapper.toCommentDto(comment), commentDto);

        verify(userRepository, times(1))
                .findById(user1.getId());
        verify(itemRepository, times(1))
                .findById(item1.getId());
        verify(bookingRepository, times(1))
                .getLastBookings(any(), any(), any());
        verify(commentRepository, times(1))
                .save(any());

        List<Booking> empty = new ArrayList<>();
        when(bookingRepository.getLastBookings(any(), any(), any()))
                .thenReturn(empty);

        assertThrows(CommentCreateException.class,() -> itemService.createComment(new ItemInfoDto.CommentDto(1L, "text",
                        user1.getName(), LocalDateTime.of(2020, 1, 1, 0, 0)),
                user1.getId(), item1.getId()));
    }
}
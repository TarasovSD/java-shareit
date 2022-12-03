package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private ItemRepository itemRepository;

    private UserRepository userRepository;

    private BookingRepository bookingRepository;

    private BookingService bookingService;

    private User user1;

    private User user2;

    private Item item1;

    private Item item2;

    private Booking booking1;

    private Booking booking4;

    private Booking approvedBooking;

    private ItemInfoDto.BookingDto bookingDto1;
    private ItemInfoDto.BookingDto bookingDto2;

    private ItemInfoDto.BookingDto bookingDto3;

    private BookingWithItemNameDto bookingWithItemNameDto1;

    private BookingWithItemNameDto approvedBookingWithItemNameDto;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        user1 = new User(1L, "user 1", "user1@ya.ru");
        user2 = new User(2L, "user 2", "user2@ya.ru");
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        ItemRequest request1 = new ItemRequest(1L, "request description", 1L, created);
        ItemRequest request2 = new ItemRequest(2L, "request2 description", 1L, created);
        item1 = new Item(1L, "item 1", "description item 1", true, user1.getId(), request1.getId());
        item2 = new Item(2L, "item 2", "description item 2", false, user1.getId(), request2.getId());
        Item item3 = new Item(3L, "item 3", "description item 3", true, user2.getId(), request2.getId());
        LocalDateTime startNextBooking = LocalDateTime.of(2027, 1, 1, 0, 0, 0);
        LocalDateTime endNextBooking = LocalDateTime.of(2028, 1, 1, 0, 0, 0);
        LocalDateTime startBooking = LocalDateTime.of(2029, 1, 1, 0, 0, 0);
        LocalDateTime endBooking = LocalDateTime.of(2030, 1, 1, 0, 0, 0);
        booking1 = new Booking(1L, startNextBooking, endNextBooking, item1, user1, Status.WAITING);
        approvedBooking = new Booking(1L, startNextBooking, endNextBooking, item1, user1, Status.APPROVED);
        Booking booking2 = new Booking(2L, startBooking, endBooking, item2, user1, Status.WAITING);
        Booking booking3 = new Booking(3L, endNextBooking, startNextBooking, item1, user1, Status.WAITING);
        booking4 = new Booking(4L, endNextBooking, startNextBooking, item3, user1, Status.WAITING);
        bookingDto1 = BookingMapper.toBookingDto(booking1);
        bookingDto2 = BookingMapper.toBookingDto(booking2);
        bookingDto3 = BookingMapper.toBookingDto(booking3);
        bookingWithItemNameDto1 = BookingMapper.toBookingDtoWithItemName(booking1, item1.getName());
        BookingWithItemNameDto bookingWithItemNameDto2 = BookingMapper.toBookingDtoWithItemName(booking2, item2.getName());
        approvedBookingWithItemNameDto = BookingMapper.toBookingDtoWithItemName(approvedBooking, item1.getName());
    }

    @Test
    void createBooking() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.save(any()))
                .thenReturn(booking1);

        final BookingWithItemNameDto bookingWithItemNameDto = bookingService.createBooking(bookingDto1, user2.getId());

        assertNotNull(bookingWithItemNameDto);
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDto);

        verify(userRepository, times(1))
                .findById(user2.getId());
        verify(itemRepository, times(2))
                .findById(any());
        verify(bookingRepository, times(1))
                .save(any());

        assertThrows(BookerIsItemOwnerException.class, () -> bookingService.createBooking(bookingDto1, user1.getId()));
        assertThrows(EndBeforeStartException.class, () -> bookingService.createBooking(bookingDto3, user1.getId()));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item2));

        assertThrows(ItemAvailableIsFalseException.class, () -> bookingService.createBooking(bookingDto2, user1.getId()));

    }

    @Test
    void approveOrRejectBookingRequest() {
        when(bookingRepository.save(any()))
                .thenReturn(booking1);
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking1));

        final BookingWithItemNameDto bookingWithItemNameDto = bookingService.approveOrRejectBookingRequest(user1.getId(),
                booking1.getId(), true);

        assertNotNull(bookingWithItemNameDto);
        assertEquals(approvedBookingWithItemNameDto, bookingWithItemNameDto);

        verify(bookingRepository, times(1))
                .save(any());
        verify(bookingRepository, times(1))
                .findById(any());

        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking4));

        assertThrows(UserNotFoundException.class, () -> bookingService.approveOrRejectBookingRequest(user1.getId(),
                booking4.getId(), true));

        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(approvedBooking));

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.approveOrRejectBookingRequest(user1.getId(),
                approvedBooking.getId(), true));
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking1));

        Optional<BookingWithItemNameDto> bookingWithItemNameDto =
                bookingService.getBookingById(booking1.getId(), user1.getId());

        assertNotNull(bookingWithItemNameDto);
        assertEquals(Optional.ofNullable(bookingWithItemNameDto1), bookingWithItemNameDto);

        verify(bookingRepository, times(1))
                .findById(any());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(booking1.getId(), user2.getId()));
    }

    @Test
    void getListOfBookingsByUserId() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(user1.getId(), PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getByBookerCurrent(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getByBookerFuture(any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingsByBooker_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.WAITING, PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingsByBooker_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED, PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getLastBookingsByBooker(any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingWithItemNameDto> bookingWithItemNameDtoList = bookingService.getListOfBookingsByUserId(user1.getId(),
                "ALL", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoList.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoList.get(0));

        verify(bookingRepository, times(1))
                .findByBooker_IdOrderByStartDesc(user1.getId(), PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListCurrent =
                bookingService.getListOfBookingsByUserId(user1.getId(), "CURRENT", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListCurrent.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListCurrent.get(0));

        verify(bookingRepository, times(1))
                .getByBookerCurrent(any(), any(), any(), any());

        List<BookingWithItemNameDto> bookingWithItemNameDtoListFuture =
                bookingService.getListOfBookingsByUserId(user1.getId(), "FUTURE", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListFuture);
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListFuture.get(0));

        verify(bookingRepository, times(1))
                .getByBookerFuture(any(), any(), any());

        List<BookingWithItemNameDto> bookingWithItemNameDtoListWaiting =
                bookingService.getListOfBookingsByUserId(user1.getId(), "WAITING", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListWaiting);
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListWaiting.get(0));

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.WAITING, PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListRejected =
                bookingService.getListOfBookingsByUserId(user1.getId(), "REJECTED", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListRejected);
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListRejected.get(0));

        verify(bookingRepository, times(1))
                .findBookingsByBooker_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED, PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListPast =
                bookingService.getListOfBookingsByUserId(user1.getId(), "PAST", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListPast);
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListPast.get(0));

        verify(bookingRepository, times(1))
                .getLastBookingsByBooker(any(), any(), any());
    }

    @Test
    void getListOfBookingsAllItemsByUserId() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(bookingRepository.findByItem_IdOrderByStartDesc(item1.getId(), PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getByItemIdCurrent(any(), any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getByItemIdFuture(any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingsByItem_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.WAITING, PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingsByItem_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED, PageRequest.of(0, 15)))
                .thenReturn(List.of(booking1));
        when(bookingRepository.getLastBookingsByItem(any(), any(), any()))
                .thenReturn(List.of(booking1));
        when(itemRepository.findAllByOwnerId(user1.getId(), PageRequest.of(0, 15)))
                .thenReturn(List.of(item1));

        List<BookingWithItemNameDto> bookingWithItemNameDtoList = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "ALL", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoList.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoList.get(0));

        verify(bookingRepository, times(1))
                .findByItem_IdOrderByStartDesc(item1.getId(), PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListCurrent
                = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "CURRENT", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListCurrent.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListCurrent.get(0));

        verify(bookingRepository, times(1))
                .getByItemIdCurrent(any(), any(), any(), any());

        List<BookingWithItemNameDto> bookingWithItemNameDtoListFuture
                = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "FUTURE", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListFuture.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListFuture.get(0));

        verify(bookingRepository, times(1))
                .getByItemIdFuture(any(), any(), any());

        List<BookingWithItemNameDto> bookingWithItemNameDtoListWaiting
                = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "WAITING", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListWaiting.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListWaiting.get(0));

        verify(bookingRepository, times(1))
                .findBookingsByItem_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.WAITING, PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListRejected
                = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "REJECTED", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListRejected.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListRejected.get(0));

        verify(bookingRepository, times(1))
                .findBookingsByItem_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.REJECTED, PageRequest.of(0, 15));

        List<BookingWithItemNameDto> bookingWithItemNameDtoListPast
                = bookingService.getListOfBookingsAllItemsByUserId(user1.getId(),
                "PAST", PageRequest.of(0, 15));

        assertNotNull(bookingWithItemNameDtoListPast.get(0));
        assertEquals(bookingWithItemNameDto1, bookingWithItemNameDtoListPast.get(0));

        verify(bookingRepository, times(1))
                .getLastBookingsByItem(any(), any(), any());
    }
}
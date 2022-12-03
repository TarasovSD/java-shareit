package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Autowired
    BookingRepository bookingRepository;

    private User user1;

    private Item item1;

    private Booking booking1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@ya.ru"));
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        ItemRequest request1 = itemRequestRepository.save(new ItemRequest(1L, "request1 description", 1L, created));
        item1 = itemRepository.save(new Item(1L, "item 1", "description item 1", true,
                user1.getId(), request1.getId()));
        LocalDateTime startBooking = LocalDateTime.of(2029, 1, 1, 0, 0, 0);
        LocalDateTime endBooking = LocalDateTime.of(2030, 1, 1, 0, 0, 0);
        booking1 = bookingRepository.save(new Booking(1L, startBooking, endBooking, item1, user1, Status.WAITING));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findByBooker_IdOrderByStartDesc() {
        final List<Booking> bookingList = bookingRepository.findByBooker_IdOrderByStartDesc(user1.getId(),
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByBookerCurrent() {
        LocalDateTime forTest = LocalDateTime.of(2029, 6, 1, 0, 0, 0);
        final List<Booking> bookingList = bookingRepository.getByBookerCurrent(user1.getId(), forTest, forTest,
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByBookerFuture() {
        LocalDateTime forTest = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getByBookerFuture(user1.getId(), forTest,
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByItemIdAndStatus() {
        final List<Booking> bookingList = bookingRepository.findBookingsByBooker_IdAndAndStatusOrderByStartDesc(user1.getId(), Status.WAITING,
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findByItem_IdOrderByStartDesc() {
        final List<Booking> bookingList = bookingRepository.findByItem_IdOrderByStartDesc(item1.getId(),
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByItemIdCurrent() {
        LocalDateTime forTest = LocalDateTime.of(2029, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getByItemIdCurrent(item1.getId(), forTest, forTest,
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByItemIdFuture() {

        LocalDateTime forTest = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getByItemIdFuture(item1.getId(), forTest,
                PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getByItemIdEndStatus() {
        final List<Booking> bookingList = bookingRepository.findBookingsByItem_IdAndAndStatusOrderByStartDesc(item1.getId(), Status.WAITING, PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getLastByItemId() {
        LocalDateTime forTest = LocalDateTime.of(2030, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getLastByItemId(item1.getId(), forTest);

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getNextByItemId() {
        LocalDateTime forTest = LocalDateTime.of(2025, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getNextByItemId(item1.getId(), forTest);

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getLastBookings() {
        LocalDateTime forTest = LocalDateTime.of(2030, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getLastBookings(item1.getId(), user1.getId(), forTest);

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getLastBookingsByBooker() {
        LocalDateTime forTest = LocalDateTime.of(2030, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getLastBookingsByBooker(user1.getId(),
                forTest, PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getLastBookingsByItem() {
        LocalDateTime forTest = LocalDateTime.of(2030, 6, 1, 0, 0, 0);

        final List<Booking> bookingList = bookingRepository.getLastBookingsByItem(item1.getId(),
                forTest, PageRequest.of(0, 15));

        assertNotNull(bookingList.get(0));
        assertEquals(1, bookingList.size());
        assertEquals(booking1, bookingList.get(0));
    }
}
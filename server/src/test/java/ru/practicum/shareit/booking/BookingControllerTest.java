package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    UserService userService;

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    private Booking booking1;

    private BookingWithItemNameDto bookingWithItemNameDto1;

    private BookingWithItemNameDto updatedBookingWithItemNameDto1;

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user 1", "user1@ya.ru");
        User user2 = new User(2L, "user 2", "user2@ya.ru");
        LocalDateTime start = LocalDateTime.of(2027, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2028, 1, 1, 0, 0, 0);
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        ItemRequest request1 = new ItemRequest(1L, "request description", 1L, created);
        Item item1 = new Item(1L, "item", "item description", true, 1L, request1.getId());
        booking1 = new Booking(1L, start, end, item1, user2, Status.WAITING);
        ItemInfoDto.BookingDto bookingDto1 = BookingMapper.toBookingDto(booking1);
        bookingWithItemNameDto1 = BookingMapper.toBookingDtoWithItemName(booking1, item1.getName());
        Booking updatedBooking1 = new Booking(1L, start, end, item1, user2, Status.APPROVED);
        updatedBookingWithItemNameDto1 = BookingMapper.toBookingDtoWithItemName(updatedBooking1, item1.getName());

    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(any(), any()))
                .thenReturn(bookingWithItemNameDto1);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingWithItemNameDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"start\":\"2027-01-01T00:00:00\"," +
                        "\"end\":\"2028-01-01T00:00:00\",\"item\":{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"ownerId\":1,\"requestId\":1}," +
                        "\"booker\":{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"},\"status\":\"WAITING\"," +
                        "\"itemName\":\"item\"}"));

        verify(bookingService, times(1))
                .createBooking(any(), any());
    }

    @Test
    void approveOrRejectBookingRequest() throws Exception {
        when(bookingService.approveOrRejectBookingRequest(any(), any(), any()))
                .thenReturn(updatedBookingWithItemNameDto1);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(updatedBookingWithItemNameDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"start\":\"2027-01-01T00:00:00\"," +
                        "\"end\":\"2028-01-01T00:00:00\",\"item\":{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"ownerId\":1,\"requestId\":1}," +
                        "\"booker\":{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"},\"status\":\"APPROVED\"," +
                        "\"itemName\":\"item\"}"));

        verify(bookingService, times(1))
                .approveOrRejectBookingRequest(any(), any(), any());
    }


    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(user1.getId(), booking1.getId()))
                .thenReturn(Optional.of(bookingWithItemNameDto1));

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"start\":\"2027-01-01T00:00:00\"," +
                        "\"end\":\"2028-01-01T00:00:00\",\"item\":{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"ownerId\":1,\"requestId\":1}," +
                        "\"booker\":{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"},\"status\":\"WAITING\"," +
                        "\"itemName\":\"item\"}"));

        verify(bookingService, times(1))
                .getBookingById(user1.getId(), booking1.getId());
    }

    @Test
    void getListOfBookingsByUserId() throws Exception {

        when(bookingService.getListOfBookingsByUserId(any(), any(), any()))
                .thenReturn(List.of(bookingWithItemNameDto1));

        mockMvc.perform(get("/bookings?state=ALL&from=0&size=10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"start\":\"2027-01-01T00:00:00\"," +
                        "\"end\":\"2028-01-01T00:00:00\",\"item\":{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"ownerId\":1,\"requestId\":1}," +
                        "\"booker\":{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"},\"status\":\"WAITING\"," +
                        "\"itemName\":\"item\"}]"));

        verify(bookingService, times(1))
                .getListOfBookingsByUserId(any(), any(), any());
    }

    @Test
    void getListOfBookingsAllItemsByUserId() throws Exception {
        when(bookingService.getListOfBookingsAllItemsByUserId(user1.getId(), "ALL", PageRequest.of(0, 15)))
                .thenReturn(List.of(bookingWithItemNameDto1));

        mockMvc.perform(get("/bookings/owner?state=ALL&from=0&size=15")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"start\":\"2027-01-01T00:00:00\"," +
                        "\"end\":\"2028-01-01T00:00:00\",\"item\":{\"id\":1,\"name\":\"item\"," +
                        "\"description\":\"item description\",\"available\":true,\"ownerId\":1,\"requestId\":1}," +
                        "\"booker\":{\"id\":2,\"name\":\"user 2\",\"email\":\"user2@ya.ru\"},\"status\":\"WAITING\"," +
                        "\"itemName\":\"item\"}]"));

        verify(bookingService, times(1))
                .getListOfBookingsAllItemsByUserId(user1.getId(), "ALL", PageRequest.of(0, 15));
    }
}
package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingWithItemNameDto createBooking(BookingDto bookingDto, Long bookerID);

    BookingWithItemNameDto approveOrRejectBookingRequest(Long userID, Long bookingId, Boolean approved);

    Optional<BookingWithItemNameDto> getBookingById(Long bookingId, Long userId);

    List<BookingWithItemNameDto> getListOfBookingsByUserId(Long bookerId, String state);

    List<BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(Long userId, String state);
}

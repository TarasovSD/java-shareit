package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingWithItemNameDto createBooking(ItemInfoDto.BookingDto bookingDto, Long bookerID);

    BookingWithItemNameDto approveOrRejectBookingRequest(Long userID, Long bookingId, Boolean approved);

    Optional<BookingWithItemNameDto> getBookingById(Long bookingId, Long userId);

    List<BookingWithItemNameDto> getListOfBookingsByUserId(Long bookerId, String state, PageRequest pageRequest);

    List<BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(Long userId, String state, PageRequest pageRequest);
}

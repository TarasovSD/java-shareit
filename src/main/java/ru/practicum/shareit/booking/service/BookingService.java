package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    ItemInfoDto.BookingWithItemNameDto createBooking(ItemInfoDto.BookingDto bookingDto, Long bookerID);

    ItemInfoDto.BookingWithItemNameDto approveOrRejectBookingRequest(Long userID, Long bookingId, Boolean approved);

    Optional<ItemInfoDto.BookingWithItemNameDto> getBookingById(Long bookingId, Long userId);

    List<ItemInfoDto.BookingWithItemNameDto> getListOfBookingsByUserId(Long bookerId, String state);

    List<ItemInfoDto.BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(Long userId, String state);
}

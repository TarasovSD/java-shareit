package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(ItemInfoDto.BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus());
    }

    public static ItemInfoDto.BookingDto toBookingDto(Booking booking) {
        return new ItemInfoDto.BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static BookingWithItemNameDto toBookingDtoWithItemName(Booking booking, String itemName) {
        return new BookingWithItemNameDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus(),
                itemName);
    }
}

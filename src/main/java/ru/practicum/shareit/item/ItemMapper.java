package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user.getId(),
                itemDto.getRequest());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest());
    }

    public static ShortItemDto toShortItemDto(Item item) {
        return new ShortItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static ItemInfoDto toItemInfoDto(Item item, Booking lastBooking, Booking nextBooking,
                                            List<ItemInfoDto.CommentDto> listOfComments) {
        ItemInfoDto.ItemBookingDto lastBookingDto = null;
        ItemInfoDto.ItemBookingDto nextBookingDto = null;
        if (lastBooking != null) {
            lastBookingDto = new ItemInfoDto.ItemBookingDto(lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd());
        }
        if (nextBooking != null) {
            nextBookingDto = new ItemInfoDto.ItemBookingDto(nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd());
        }
        return new ItemInfoDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest(),
                lastBookingDto,
                nextBookingDto,
                listOfComments);
    }
}

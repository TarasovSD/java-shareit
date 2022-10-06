package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user, itemDto.getRequest());
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
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userID);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userID);

    Optional<ItemDto> getItemById(Long itemId);

    List<ShortItemDto> getItemsListByUserId(Long userId);

    List<ShortItemDto> getItemsListBySearch(String text);
}

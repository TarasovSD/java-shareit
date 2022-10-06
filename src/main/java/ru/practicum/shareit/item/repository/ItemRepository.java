package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Optional<ItemDto> createItem(Item item);

    Optional<ItemDto> updateItem(Item item, Long itemId);

    Optional<ItemDto> getItemById(Long itemId);

    List<ShortItemDto> getItemsListByUserId(Long userId);

    List<ShortItemDto> getItemsListBySearch(String text);
}

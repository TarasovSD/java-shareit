package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userID);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userID);

    Optional<ItemInfoDto> getItemById(Long itemId, Long userId);

    List<ItemInfoDto> getItemsListByUserId(Long userId);

    List<ShortItemDto> getItemsListBySearch(String text);

    CommentDto createComment(CommentDto commentDto, Long userID, Long itemId);
}

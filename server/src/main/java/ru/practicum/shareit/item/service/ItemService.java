package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userID);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long userID);

    Optional<ItemInfoDto> getItemById(Long itemId, Long userId);

    List<ItemInfoDto> getItemsListByUserId(Long userId, PageRequest pageRequest);

    List<ShortItemDto> getItemsListBySearch(String text, PageRequest pageRequest);

    ItemInfoDto.CommentDto createComment(ItemInfoDto.CommentDto commentDto, Long userID, Long itemId);
}

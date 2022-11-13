package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long requestorId, LocalDateTime created) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), requestorId, created);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequestDtoWithResponses toItemRequestDtoWithResponses(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoWithResponses(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }
}

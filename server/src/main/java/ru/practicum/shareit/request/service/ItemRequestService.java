package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorID);

    List<ItemRequestDtoWithResponses> getListOfOwnRequests(Long userid);

    List<ItemRequestDtoWithResponses> getListOfAllRequests(PageRequest pageRequest, Long userId);

    Optional<ItemRequestDtoWithResponses> getRequestById(Long requestId, Long userId);
}

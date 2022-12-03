package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorID) {
        User user = userRepository.findById(requestorID).get();
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequestForSave = ItemRequestMapper.toItemRequest(itemRequestDto, requestorID, created);
        itemRequestRepository.save(itemRequestForSave);
        return ItemRequestMapper.toItemRequestDto(itemRequestForSave);
    }

    @Override
    public List<ItemRequestDtoWithResponses> getListOfOwnRequests(Long userid) {
        User user = userRepository.findById(userid).get();
        List<ItemRequestDtoWithResponses> allOwnRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.getByRequestorId(userid)) {
            List<ItemRequestDtoWithResponses.ItemDtoForRequest> listOfResponses = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemDtoForRequest)
                    .collect(Collectors.toList());
            allOwnRequests.add(ItemRequestMapper.toItemRequestDtoWithResponses(itemRequest, listOfResponses));
        }
        return allOwnRequests;
    }

    @Override
    public List<ItemRequestDtoWithResponses> getListOfAllRequests(PageRequest pageRequest, Long userId) {
        List<ItemRequest> listOfRequests = itemRequestRepository.findAll(pageRequest)
                .stream()
                .collect(Collectors.toList());
        List<ItemRequestDtoWithResponses> listOfRequestsOtherUsers = new ArrayList<>();
        for (ItemRequest itemRequest : listOfRequests) {
            List<ItemRequestDtoWithResponses.ItemDtoForRequest> listOfResponses = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                    .map(ItemMapper::toItemDtoForRequest)
                    .collect(Collectors.toList());
            if (!Objects.equals(itemRequest.getRequestorId(), userId)) {
                listOfRequestsOtherUsers.add(ItemRequestMapper.toItemRequestDtoWithResponses(itemRequest, listOfResponses));
            }
        }
        return listOfRequestsOtherUsers;
    }

    @Override
    public Optional<ItemRequestDtoWithResponses> getRequestById(Long requestId, Long userId) {
        User user = userRepository.findById(userId).get();
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        List<ItemRequestDtoWithResponses.ItemDtoForRequest> listOfResponses = itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDtoForRequest)
                .collect(Collectors.toList());
        ItemRequestDtoWithResponses itemRequestDtoWithResponses =
                ItemRequestMapper.toItemRequestDtoWithResponses(itemRequest, listOfResponses);
        return Optional.of(itemRequestDtoWithResponses);

    }
}

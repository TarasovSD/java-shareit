package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    private ItemRequestService requestService;

    private ItemRequestRepository requestRepository;

    private UserRepository userRepository;

    private ItemRepository itemRepository;

    private User user1;

    private ItemRequest request1;

    private ItemRequestDto itemRequestDto1;

    private ItemRequestDtoWithResponses itemRequestDtoWithResponses1;

    private Item item1;

    @BeforeEach
    void setUp() {
        requestRepository = mock(ItemRequestRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        requestService = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository);
        user1 = new User(1L, "user 1", "user1@ya.ru");
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        request1 = new ItemRequest(1L, "request description", 1L, created);
        item1 = new Item(1L, "item", "item description", true, 1L, request1.getId());
        ItemRequestDtoWithResponses.ItemDtoForRequest itemDto1 = ItemMapper.toItemDtoForRequest(item1);
        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(request1);
        itemRequestDtoWithResponses1 = ItemRequestMapper.toItemRequestDtoWithResponses(request1, List.of(itemDto1));
    }

    @Test
    void createRequest() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(requestRepository.save(request1))
                .thenReturn(request1);

        final ItemRequestDto itemRequestDtoForTest = requestService.createRequest(itemRequestDto1, user1.getId());

        assertNotNull(itemRequestDtoForTest);
        assertEquals(1L, itemRequestDtoForTest.getId());
        assertEquals("request description", itemRequestDtoForTest.getDescription());
        assertNotNull(itemRequestDtoForTest.getCreated());

        verify(userRepository, times(1))
                .findById(user1.getId());
        verify(requestRepository, times(1))
                .save(any());
    }

    @Test
    void getListOfOwnRequests() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(requestRepository.getByRequestorId(user1.getId()))
                .thenReturn(List.of(request1));
        when(itemRepository.findAllByRequestId(request1.getId()))
                .thenReturn(List.of(item1));

        final List<ItemRequestDtoWithResponses> itemRequestDtoWithResponsesList =
                requestService.getListOfOwnRequests(user1.getId());

        assertNotNull(itemRequestDtoWithResponsesList);
        assertNotNull(itemRequestDtoWithResponsesList.get(0));
        assertEquals(1, itemRequestDtoWithResponsesList.size());
        assertEquals(itemRequestDtoWithResponses1, itemRequestDtoWithResponsesList.get(0));

        verify(userRepository, times(1))
                .findById(user1.getId());
        verify(itemRepository, times(1))
                .findAllByRequestId(request1.getId());
        verify(requestRepository, times(1))
                .getByRequestorId(user1.getId());
    }

    @Test
    void getListOfAllRequests() {
        Page<ItemRequest> itemRequests = new PageImpl<>(List.of(request1), PageRequest.of(0, 15), List.of(request1).size());
        when(requestRepository.findAll(PageRequest.of(0, 15)))
                .thenReturn(itemRequests);
        when(itemRepository.findAllByRequestId(request1.getId()))
                .thenReturn(List.of(item1));

        final List<ItemRequestDtoWithResponses> itemRequestDtoWithResponsesList =
                requestService.getListOfAllRequests(PageRequest.of(0, 15), 2L);

        assertNotNull(itemRequestDtoWithResponsesList.get(0));
        assertEquals(1, itemRequestDtoWithResponsesList.size());
        assertEquals(itemRequestDtoWithResponses1, itemRequestDtoWithResponsesList.get(0));

        verify(requestRepository, times(1))
                .findAll(PageRequest.of(0, 15));
        verify(itemRepository, times(1))
                .findAllByRequestId(request1.getId());
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(requestRepository.findById(user1.getId()))
                .thenReturn(Optional.of(request1));
        when(itemRepository.findAllByRequestId(request1.getId()))
                .thenReturn(List.of(item1));

        final Optional<ItemRequestDtoWithResponses> itemRequestDtoWithResponses =
                requestService.getRequestById(request1.getId(), user1.getId());

        assertNotNull(itemRequestDtoWithResponses.get());
        assertEquals(itemRequestDtoWithResponses1, itemRequestDtoWithResponses.get());

        verify(requestRepository, times(1))
                .findById(user1.getId());
        verify(itemRepository, times(1))
                .findAllByRequestId(request1.getId());
        verify(userRepository, times(1))
                .findById(user1.getId());
    }
}
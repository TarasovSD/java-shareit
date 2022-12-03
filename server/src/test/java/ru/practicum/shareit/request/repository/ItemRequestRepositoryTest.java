package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private User user1;

    private ItemRequest request1;


    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user 1", "user1@ya.ru"));
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        request1 = itemRequestRepository.save(new ItemRequest(1L, "request1 description", 1L, created));
    }

    @Test
    void getByRequestorId() {
        final List<ItemRequest> itemRequestList = itemRequestRepository.getByRequestorId(user1.getId());

        assertNotNull(itemRequestList.get(0));
        assertEquals(1, itemRequestList.size());
        assertEquals(request1, itemRequestList.get(0));
    }
}
package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    private Item item1;

    private User user1;

    private ItemRequest request1;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user 1", "user1@ya.ru");
        userRepository.save(user1);
        LocalDateTime created = LocalDateTime.of(2022, 11, 14, 23, 24, 30);
        request1 = new ItemRequest(1L, "request1 description", 1L, created);
        itemRequestRepository.save(request1);
        item1 = new Item(1L, "item 1", "description item 1", true,
                user1.getId(), request1.getId());
                itemRepository.save(item1);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findAllByOwnerId() {
        final List<Item> byOwner = itemRepository.findAllByOwnerId(user1.getId(), PageRequest.of(0, 15));

        assertNotNull(byOwner.get(0));
        assertEquals(1, byOwner.size());
        assertEquals(item1, byOwner.get(0));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void findAllByRequestId() {
        final List<Item> byRequest = itemRepository.findAllByRequestId(request1.getId());

        assertNotNull(byRequest.get(0));
        assertEquals(1, byRequest.size());
        assertEquals(item1, byRequest.get(0));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getAll() {
        final List<Item> allItems = itemRepository.getAll(PageRequest.of(0, 15));

        assertNotNull(allItems.get(0));
        assertEquals(1, allItems.size());
        assertEquals(item1, allItems.get(0));
    }
}
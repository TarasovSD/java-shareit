package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryInMemoryInMemoryImpl implements ItemRepositoryInMemory {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generatorId = 1L;

    @Override
    public Item createItem(Item item) {
        item.setId(generatorId);
        items.put(item.getId(), item);
        generatorId++;
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsListByUserId(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (Objects.equals(entry.getValue().getOwnerId(), userId)) {
                Item item = entry.getValue();
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> getItemsListBySearch() {
        List<Item> foundItems = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            foundItems.add(entry.getValue());
        }
        return foundItems;
    }
}

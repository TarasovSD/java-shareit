package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.ItemAvailableIsNullException;
import ru.practicum.shareit.exceptions.ItemIsNullException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long generatorId = 1L;

    @Override
    public Optional<ItemDto> createItem(Item item) {
        validateItem(item);
        item.setId(generatorId);
        items.put(item.getId(), item);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        generatorId++;
        return Optional.of(itemDto);
    }

    @Override
    public Optional<ItemDto> updateItem(Item item, Long itemId) {
        Optional<ItemDto> foundItemDto = getItemById(itemId);
        Item itemForGetOwner = null;
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (Objects.equals(entry.getKey(), itemId)) {
                itemForGetOwner = entry.getValue();
            }
        }
        assert itemForGetOwner != null;
        Item foundItem = ItemMapper.toItem(foundItemDto.get(), itemForGetOwner.getOwner());
        if (!Objects.equals(item.getOwner().getId(), foundItem.getOwner().getId())) {
            throw new ItemNotFoundException("Вещь не найдена!");
        }
        if (item.getName() != null) {
            foundItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            foundItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            foundItem.setAvailable(item.getAvailable());
        }
        items.put(foundItem.getId(), foundItem);
        return Optional.of(ItemMapper.toItemDto(foundItem));
    }

    @Override
    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = null;
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (Objects.equals(entry.getKey(), itemId)) {
                item = entry.getValue();
            }
        }
        if (item != null) {
            return Optional.of(ItemMapper.toItemDto(item));
        } else {
            log.info("Вещь с id {} не найдена!", itemId);
            return Optional.empty();
        }
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new ItemIsNullException("Вещь = null");
        }
        if (item.getAvailable() == null) {
            throw new ItemAvailableIsNullException("Поле available = null");
        }
    }

    @Override
    public List<ShortItemDto> getItemsListByUserId(Long userId) {
        List<ShortItemDto> userItems = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (Objects.equals(entry.getValue().getOwner().getId(), userId)) {
                ShortItemDto shortItemDto = ItemMapper.toShortItemDto(entry.getValue());
                userItems.add(shortItemDto);
            }
        }
        return userItems;
    }

    @Override
    public List<ShortItemDto> getItemsListBySearch(String text) {
        List<ShortItemDto> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            String searchText = text.toLowerCase();
            String nameToLowerCase = entry.getValue().getName().toLowerCase();
            String descriptionToLowerCase = entry.getValue().getDescription().toLowerCase();
            if (nameToLowerCase.contains(searchText) || descriptionToLowerCase.contains(searchText)) {
                if (entry.getValue().getAvailable()) {
                    foundItems.add(ItemMapper.toShortItemDto(entry.getValue()));
                }
            }
        }
        return foundItems;
    }
}

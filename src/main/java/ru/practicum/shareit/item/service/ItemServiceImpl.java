package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemAvailableIsNullException;
import ru.practicum.shareit.exceptions.ItemIsNullException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userID) {
        User user = userRepository.getUserById(userID);
        if (user != null) {
            Item item = ItemMapper.toItem(itemDto, user);
            validateItem(item);
            return ItemMapper.toItemDto(itemRepository.createItem(item));
        } else {
            throw new UserNotFoundException("Невозможно создать вещь, так как owner не найден!");
        }
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long userID) {
        User user = userRepository.getUserById(userID);
        Item item = ItemMapper.toItem(itemDto, user);
        Item foundItem = itemRepository.getItemById(itemId);
        validateItem(foundItem);
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
        return ItemMapper.toItemDto(itemRepository.updateItem(foundItem));
    }

    @Override
    public Optional<ItemDto> getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        if (item != null) {
            return Optional.of(ItemMapper.toItemDto(item));
        } else {
            log.info("Вещь с id {} не найдена!", itemId);
            return Optional.empty();
        }
    }

    @Override
    public List<ShortItemDto> getItemsListByUserId(Long userId) {
        List<Item> userItems = itemRepository.getItemsListByUserId(userId);
        List<ShortItemDto> userItemsDto = new ArrayList<>();
        for (Item item : userItems) {
            userItemsDto.add(ItemMapper.toShortItemDto(item));
        }
        return userItemsDto;
    }

    @Override
    public List<ShortItemDto> getItemsListBySearch(String text) {
        List<ShortItemDto> foundItems = new ArrayList<>();
        if (text.isBlank()) {
            return foundItems;
        }
        List<Item> foundItemsInRepository = itemRepository.getItemsListBySearch();
        for (Item item : foundItemsInRepository) {
            String searchText = text.toLowerCase();
            String nameToLowerCase = item.getName().toLowerCase();
            String descriptionToLowerCase = item.getDescription().toLowerCase();
            if (nameToLowerCase.contains(searchText) || descriptionToLowerCase.contains(searchText)) {
                if (item.getAvailable()) {
                    foundItems.add(ItemMapper.toShortItemDto(item));
                }
            }
        }
        return foundItems;
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new ItemIsNullException("Вещь = null");
        }
        if (item.getAvailable() == null) {
            throw new ItemAvailableIsNullException("Поле available = null");
        }
    }
}

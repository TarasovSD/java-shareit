package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    final ItemRepository itemRepository;
    final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<ItemDto> createItem(ItemDto itemDto, Long userID) {
        Optional<UserDto> userDto = userRepository.getUserById(userID);
        User user = null;
        if (userDto.isPresent()) {
            user = UserMapper.toUser(userDto.get());
        }
        if (user != null) {
            Item item = ItemMapper.toItem(itemDto, user);
            return itemRepository.createItem(item);
        } else {
            throw new UserNotFoundException("невозможно создать вещь, так как owner не найден!");
        }
    }

    @Override
    public Optional<ItemDto> updateItem(ItemDto itemDto, Long itemId, Long userID) {
        Optional<UserDto> userDto = userRepository.getUserById(userID);
        User user = null;
        if (userDto.isPresent()) {
            user = UserMapper.toUser(userDto.get());
        }
        Item item = ItemMapper.toItem(itemDto, user);
        return itemRepository.updateItem(item, itemId);
    }

    @Override
    public Optional<ItemDto> getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<ShortItemDto> getItemsListByUserId(Long userId) {
        return itemRepository.getItemsListByUserId(userId);
    }

    @Override
    public List<ShortItemDto> getItemsListBySearch(String text) {
        return itemRepository.getItemsListBySearch(text);
    }
}

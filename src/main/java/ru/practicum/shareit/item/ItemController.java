package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userID, @Validated({Create.class})
    @RequestBody ItemDto itemDto) {
        ItemDto createdItemDto = itemService.createItem(itemDto, userID);
        log.info("Вещь создана");
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader("X-Sharer-User-Id") Long userID, @Validated(Update.class) @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        ItemDto itemDtoForUpdate = itemService.updateItem(itemDto, itemId, userID);
        log.info("Вещь с id {} обновлена", itemId);
        return itemDtoForUpdate;
    }

    @GetMapping("/{itemId}")
    public Optional<ItemDto> getItemById(@PathVariable Long itemId) {
        log.info("Выполнен запрос getUserById по ID: " + itemId);
        Optional<ItemDto> optionalItem = itemService.getItemById(itemId);
        optionalItem.orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        return optionalItem;
    }

    @GetMapping()
    public List<ShortItemDto> getItemsListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполнен запрос списка всех вещей пользователя с ID: " + userId);
        return itemService.getItemsListByUserId(userId);
    }

    @GetMapping("/search")
    public List<ShortItemDto> getItemsListBySearch(@RequestParam String text) {
        log.info("Выполнен поиск вещей по запросу: " + text);
        return itemService.getItemsListBySearch(text);
    }
}

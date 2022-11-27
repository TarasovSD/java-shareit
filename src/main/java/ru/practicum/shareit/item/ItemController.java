package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ShortItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public Optional<ItemInfoDto> getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполнен запрос getUserById по ID: " + itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemInfoDto> getItemsListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Выполнен запрос списка всех вещей пользователя с ID: " + userId);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.getItemsListByUserId(userId, pageRequest);
    }

    @GetMapping("/search")
    public List<ShortItemDto> getItemsListBySearch(@RequestParam String text,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Выполнен поиск вещей по запросу: " + text);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.getItemsListBySearch(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public ItemInfoDto.CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userID,
                                                @RequestBody ItemInfoDto.CommentDto commentDto, @PathVariable Long itemId) {
        ItemInfoDto.CommentDto createdCommentDto = itemService.createComment(commentDto, userID, itemId);
        log.info("Комментарий создан");
        return createdCommentDto;
    }
}

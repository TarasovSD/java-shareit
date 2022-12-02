package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userID,
                                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Создание новой вещи");
        return itemClient.createItem(userID, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemById(@RequestHeader("X-Sharer-User-Id") Long userID,
                                                 @Validated(Update.class) @PathVariable Long itemId,
                                                 @RequestBody ItemDto itemDto) {
        log.info("Обновление вещи с id {}", itemId);
        return itemClient.updateItemById(userID, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос вещи по ID: " + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemsListByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Запрос списка всех вещей пользователя с ID {}, from {}, size {}", userId, from, size);
        return itemClient.getItemsListByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsListBySearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam String text,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Поиск вещей по запросу: " + text);
        return itemClient.getItemsListBySearch(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userID,
                                                @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {

        log.info("Создание комментария к вещи с ID {}", itemId);
        return itemClient.createComment(userID, itemId, commentDto);
    }
}

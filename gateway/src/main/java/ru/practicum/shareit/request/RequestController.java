package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long requestorID,
                                                @Validated({Create.class}) @RequestBody
                                                ItemRequestDto itemRequestDto) {
        log.info("Создание нового запроса вещи пользователем с ID {}", requestorID);
        return requestClient.createRequest(requestorID, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getListOfOwnRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос списка всех запосов пользователя с ID: " + userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getListOfAllRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос списка всех запосов пользователя с ID {}", userId);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение запроса с ID {} пользователем с ID {}: ", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}

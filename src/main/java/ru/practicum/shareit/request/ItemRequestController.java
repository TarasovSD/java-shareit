package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;


@Validated
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    final ItemRequestService requestService;

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping()
    public ItemRequestDto createBooking(@RequestHeader("X-Sharer-User-Id") Long requestorID,
                                        @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto createdItemRequestDto = requestService.createRequest(itemRequestDto, requestorID);
        log.info("Запрос вещи создан");
        return createdItemRequestDto;
    }

    @GetMapping()
    public List<ItemRequestDtoWithResponses> getListOfOwnRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполнен запрос списка всех запосов пользователя с ID: " + userId);
        return requestService.getListOfOwnRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithResponses> getListOfAllRequests(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Выполнен запрос списка всех запосов");
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return requestService.getListOfAllRequests(pageRequest, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithResponses getRequestById(@PathVariable Long requestId,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Выполнен запрос getRequestById по ID: " + requestId);
        Optional<ItemRequestDtoWithResponses> optionalRequest = requestService.getRequestById(requestId, userId);
        return optionalRequest.get();
    }

}

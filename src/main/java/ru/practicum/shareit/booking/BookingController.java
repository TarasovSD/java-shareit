package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    final BookingService bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public ItemInfoDto.BookingWithItemNameDto createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerID, @Validated({Create.class})
    @RequestBody ItemInfoDto.BookingDto bookingDto) {
        ItemInfoDto.BookingWithItemNameDto createdBookingDto = bookingService.createBooking(bookingDto, bookerID);
        log.info("Бронирование создано");
        return createdBookingDto;
    }

    @PatchMapping("/{bookingId}")
    public ItemInfoDto.BookingWithItemNameDto approveOrRejectBookingRequest(@RequestHeader("X-Sharer-User-Id") Long userID,
                                                                            @Validated(Update.class) @PathVariable Long bookingId,
                                                                            @RequestParam Boolean approved) {
        ItemInfoDto.BookingWithItemNameDto bookingDtoForUpdate = bookingService.approveOrRejectBookingRequest(userID, bookingId, approved);
        log.info("Бронирование с id {} обновлено. Статус бронирования: {}", bookingId, bookingDtoForUpdate.getStatus());
        return bookingDtoForUpdate;
    }

    @GetMapping("/{bookingId}")
    public Optional<ItemInfoDto.BookingWithItemNameDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                       @PathVariable Long bookingId) {
        log.info("Выполнен запрос getBookingById по ID: " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<ItemInfoDto.BookingWithItemNameDto> getListOfBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполнен запрос getListOfBookingsByUserId для пользователя с ID: " + userId);
        return bookingService.getListOfBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<ItemInfoDto.BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                                      @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполнен запрос getListOfBookingsAllItemsByUserId для пользователя с ID: " + userId);
        return bookingService.getListOfBookingsAllItemsByUserId(userId, state);
    }
}

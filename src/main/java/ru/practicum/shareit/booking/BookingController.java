package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingWithItemNameDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingWithItemNameDto createBooking(@RequestHeader("X-Sharer-User-Id") Long bookerID, @Validated({Create.class})
    @RequestBody ItemInfoDto.BookingDto bookingDto) {
        BookingWithItemNameDto createdBookingDto = bookingService.createBooking(bookingDto, bookerID);
        log.info("Бронирование создано");
        return createdBookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingWithItemNameDto approveOrRejectBookingRequest(@RequestHeader("X-Sharer-User-Id") Long userID,
                                                                @Validated(Update.class) @PathVariable Long bookingId,
                                                                @RequestParam Boolean approved) {
        BookingWithItemNameDto bookingDtoForUpdate = bookingService.approveOrRejectBookingRequest(userID, bookingId, approved);
        log.info("Бронирование с id {} обновлено. Статус бронирования: {}", bookingId, bookingDtoForUpdate.getStatus());
        return bookingDtoForUpdate;
    }

    @GetMapping("/{bookingId}")
    public Optional<BookingWithItemNameDto> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long bookingId) {
        log.info("Выполнен запрос getBookingById по ID: " + bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingWithItemNameDto> getListOfBookingsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state,
                                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Выполнен запрос getListOfBookingsByUserId для пользователя с ID: " + userId);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return bookingService.getListOfBookingsByUserId(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    public List<BookingWithItemNameDto> getListOfBookingsAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                          @RequestParam(defaultValue = "ALL") String state,
                                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                          @Positive @RequestParam(name = "size", defaultValue = "15") Integer size) {
        log.info("Выполнен запрос getListOfBookingsAllItemsByUserId для пользователя с ID: " + userId);
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return bookingService.getListOfBookingsAllItemsByUserId(userId, state, pageRequest);
    }
}

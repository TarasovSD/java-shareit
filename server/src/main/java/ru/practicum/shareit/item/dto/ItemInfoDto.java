package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private List<CommentDto> comments;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class ItemBookingDto {
        private Long id;
        private Long bookerId;
        private LocalDateTime start;
        private LocalDateTime end;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class CommentDto {

        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class BookingDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long itemId;
        private Long bookerId;
        Status status;
    }
}

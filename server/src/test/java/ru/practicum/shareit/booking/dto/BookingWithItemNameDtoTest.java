package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingWithItemNameDtoTest {

    @Autowired
    private JacksonTester<BookingWithItemNameDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        BookingWithItemNameDto bookingDto = new BookingWithItemNameDto(1L, start, end,
                new Item(1L, "item name", "description", true, 1L, 1L),
                new User(1L, "user name", "user@ya.ru"),
                Status.WAITING, "item name");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        JsonContent<BookingWithItemNameDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(formatter));
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(formatter));
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingDto.getItem().getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingDto.getItem().getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId")
                .isEqualTo(bookingDto.getItem().getOwnerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId")
                .isEqualTo(bookingDto.getItem().getRequestId().intValue());
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDto.getBooker().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingDto.getBooker().getName());
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingDto.getBooker().getEmail());
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDto.getStatus().toString());
        assertThat(result).hasJsonPath("$.itemName");
        assertThat(result).extractingJsonPathStringValue("$.itemName").isEqualTo(bookingDto.getItemName());

        BookingWithItemNameDto bookingDtoTest = json.parseObject(result.getJson());

        assertThat(bookingDtoTest).isEqualTo(bookingDto);
    }
}
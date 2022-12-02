package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemInfoDtoTest {

    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime startLast = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime endLast = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime startNext = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endNext = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

        List<ItemInfoDto.CommentDto> listOfComments = new ArrayList<>();

        ItemInfoDto.ItemBookingDto lastBooking = new ItemInfoDto.ItemBookingDto(1L, 1L, startLast, endLast);

        ItemInfoDto.ItemBookingDto nextBooking = new ItemInfoDto.ItemBookingDto(1L, 1L, startNext, endNext);

        ItemInfoDto itemDto = new ItemInfoDto(1L, "item name", "item description", true,
                1L, lastBooking, nextBooking, listOfComments);

        JsonContent<ItemInfoDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");

        ItemInfoDto itemDtoForTest = json.parseObject(result.getJson());

        assertThat(itemDtoForTest).isEqualTo(itemDto);
    }
}
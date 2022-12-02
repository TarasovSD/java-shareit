package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2020, 01, 01, 0, 0, 0);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "item request description", created);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().format(formatter));



        ItemRequestDto itemRequestDtoForTest = json.parseObject(result.getJson());

        assertThat(itemRequestDtoForTest).isEqualTo(itemRequestDto);
    }

}
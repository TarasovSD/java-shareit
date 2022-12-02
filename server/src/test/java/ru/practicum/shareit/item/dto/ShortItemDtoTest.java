package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ShortItemDtoTest {

    @Autowired
    private JacksonTester<ShortItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ShortItemDto itemDto = new ShortItemDto(1L, "item name", "item description", true);

        JsonContent<ShortItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());

        ShortItemDto itemDtoForTest = json.parseObject(result.getJson());

        assertThat(itemDtoForTest).isEqualTo(itemDto);
    }

}
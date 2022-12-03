package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ShortItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}

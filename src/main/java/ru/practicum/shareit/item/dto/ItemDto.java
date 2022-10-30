package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    private String description;
    private Boolean available;
    private Long request;
}

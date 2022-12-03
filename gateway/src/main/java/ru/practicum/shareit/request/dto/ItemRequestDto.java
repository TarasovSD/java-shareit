package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDto {
    @NotBlank(groups = {Create.class})
    private String description;
}

package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDto {
    @NotBlank(groups = {Create.class})
    private String text;
}

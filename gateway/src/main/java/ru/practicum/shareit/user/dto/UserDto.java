package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDto {

    private Long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    private String email;
}

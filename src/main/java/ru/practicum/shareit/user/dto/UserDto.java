package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.user.Create;

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
    @Email(groups = {Create.class})
    private String email;
}

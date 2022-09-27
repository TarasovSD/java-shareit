package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Data
public class Item {
    @NonNull
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
    @NotBlank
    @NonNull
    private User owner;
    private String request;
}

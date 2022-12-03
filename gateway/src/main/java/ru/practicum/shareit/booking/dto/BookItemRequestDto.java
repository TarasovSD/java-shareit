package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.booking.annotations.StartBeforeEnd;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEnd(groups = {Create.class, Update.class})
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}

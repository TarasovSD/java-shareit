package ru.practicum.shareit.booking.annotations;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class StartBeforeEndProcessor implements ConstraintValidator<StartBeforeEnd, BookItemRequestDto> {

    public void initialize(StartBeforeEnd constraintAnnotation) {
    }

    public boolean isValid(BookItemRequestDto bookItemRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        log.info(bookItemRequestDto.toString());
        return bookItemRequestDto.getStart().isBefore(bookItemRequestDto.getEnd());
    }
}

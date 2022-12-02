package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

    @Getter
    private static class ErrorDto {
        private final String error;

        public ErrorDto(String error) {
            this.error = error;
        }
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(new ErrorDto("Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }
}

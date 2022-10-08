package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(value = UserEmailAlreadyExistsException.class)
    public ResponseEntity<String> handleUserEmailAlreadyExistsException(final UserEmailAlreadyExistsException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Пользователь с таким email уже есть в базе", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = UserEmailIsNullException.class)
    public ResponseEntity<String> handleUserEmailIsNullException(final UserEmailIsNullException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Email пользователя = null", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(final UserNotFoundException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Пользователь не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ItemIsNullException.class)
    public ResponseEntity<String> handleItemIsNullException(final ItemIsNullException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Вещь не найдена", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(final ItemNotFoundException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Вещь не найдена", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ItemAvailableIsNullException.class)
    public ResponseEntity<String> handleItemAvailableIsNullException(final ItemAvailableIsNullException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Поле available = null", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Произошла непредвиденная ошибка ", HttpStatus.BAD_REQUEST);
    }
}

package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

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

    @ExceptionHandler(value = RequestNotFoundException.class)
    public ResponseEntity<String> handleRequestNotFoundException(final RequestNotFoundException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Запрос не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ItemAvailableIsNullException.class)
    public ResponseEntity<String> handleItemAvailableIsNullException(final ItemAvailableIsNullException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Поле available = null", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ItemAvailableIsFalseException.class)
    public ResponseEntity<String> handleItemAvailableIsFalseException(final ItemAvailableIsFalseException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Бронь невозможна, так как поле available = false", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EndBeforeStartException.class)
    public ResponseEntity<String> handleEndBeforeStartException(final EndBeforeStartException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Значение поля End не может быть раньше значения поля Start", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidValueOfStateParameterException.class)
    public ResponseEntity<ErrorDto> handleInvalidValueOfStateParameterException(final InvalidValueOfStateParameterException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(new ErrorDto("Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }

    @Getter
    private static class ErrorDto {
        private final String error;

        public ErrorDto(String error) {
            this.error = error;
        }
    }

    @ExceptionHandler(value = BookingAlreadyApprovedException.class)
    public ResponseEntity<String> handleBookingAlreadyApprovedException(final BookingAlreadyApprovedException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Бронирование уже подтверждено", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = BookerIsItemOwnerException.class)
    public ResponseEntity<String> handleBookerIsItemOwnerException(final BookerIsItemOwnerException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Попытка забронировать вещь владельцем", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFoundException(final BookingNotFoundException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Бронирование не найдено", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = CommentCreateException.class)
    public ResponseEntity<String> handleCommentCreateException(final CommentCreateException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Создание комментария невозможно, так как не найден автор или вещь",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Произошла непредвиденная ошибка", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Попытка ввести уже существующие данные с уникальным значением",
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNoSuchElementException(final NoSuchElementException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>("Объект отсутствует в БД", HttpStatus.NOT_FOUND);
    }
}

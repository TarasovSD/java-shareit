package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.UserRepository;

@RestControllerAdvice(assignableTypes = {UserRepository.class})
public class AllExceptionHandler extends RuntimeException{

    @ExceptionHandler(value = UserEmailAlreadyExistsException.class)
    public ResponseEntity<String> handleUserEmailAlreadyExistsException() {
        return new ResponseEntity<>("Conflict", HttpStatus.CONFLICT);

    }

    @ExceptionHandler(value = UserEmailIsNullException.class)
    public ResponseEntity<String> handleUserEmailIsNullException() {
        return new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST);
    }
}

package ru.practicum.shareit.exceptions;

public class UserEmailIsNullException extends RuntimeException {
    public UserEmailIsNullException(String message) {
        super(message);
    }
}

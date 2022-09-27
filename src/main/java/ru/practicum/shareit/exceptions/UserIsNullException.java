package ru.practicum.shareit.exceptions;

public class UserIsNullException extends RuntimeException {
    public UserIsNullException(String message) {
        super(message);
    }
}

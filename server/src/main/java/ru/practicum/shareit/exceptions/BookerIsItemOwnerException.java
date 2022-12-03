package ru.practicum.shareit.exceptions;

public class BookerIsItemOwnerException extends RuntimeException {
    public BookerIsItemOwnerException(String message) {
        super(message);
    }
}


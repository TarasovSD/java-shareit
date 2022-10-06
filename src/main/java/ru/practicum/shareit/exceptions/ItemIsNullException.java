package ru.practicum.shareit.exceptions;

public class ItemIsNullException extends RuntimeException {
    public ItemIsNullException(String message) {
        super(message);
    }
}

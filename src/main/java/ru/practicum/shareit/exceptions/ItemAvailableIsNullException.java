package ru.practicum.shareit.exceptions;

public class ItemAvailableIsNullException extends RuntimeException {
    public ItemAvailableIsNullException(String message) {
        super(message);
    }
}

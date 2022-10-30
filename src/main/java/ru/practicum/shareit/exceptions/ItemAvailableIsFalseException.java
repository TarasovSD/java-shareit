package ru.practicum.shareit.exceptions;

public class ItemAvailableIsFalseException extends RuntimeException {
    public ItemAvailableIsFalseException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exceptions;

public class EndBeforeStartException extends RuntimeException{
    public EndBeforeStartException(String message) {
        super(message);
    }
}

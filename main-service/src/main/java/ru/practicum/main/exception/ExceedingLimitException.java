package ru.practicum.main.exception;

public class ExceedingLimitException extends RuntimeException {
    public ExceedingLimitException(String message) {
        super(message);
    }
}

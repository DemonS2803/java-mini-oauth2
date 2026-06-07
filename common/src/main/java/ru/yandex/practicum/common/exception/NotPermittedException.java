package ru.yandex.practicum.common.exception;

public class NotPermittedException extends RuntimeException {
    public NotPermittedException(String message) {
        super(message);
    }
}

package ru.yandex.practicum.common.exception;

public class AuthenticateException extends RuntimeException {
    public AuthenticateException(String message) {
        super(message);
    }
}

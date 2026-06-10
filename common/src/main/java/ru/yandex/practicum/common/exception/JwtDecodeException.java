package ru.yandex.practicum.common.exception;

public class JwtDecodeException extends RuntimeException {
    public JwtDecodeException(String message) {
        super(message);
    }
}

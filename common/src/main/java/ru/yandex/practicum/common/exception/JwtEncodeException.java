package ru.yandex.practicum.common.exception;

public class JwtEncodeException extends RuntimeException {
    public JwtEncodeException(String message) {
        super(message);
    }
}

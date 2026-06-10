package ru.yandex.practicum.common.exception;

public class JWTSignInvalidException extends RuntimeException {
    public JWTSignInvalidException() {
        super("JWT sign is invalid");
    }
}

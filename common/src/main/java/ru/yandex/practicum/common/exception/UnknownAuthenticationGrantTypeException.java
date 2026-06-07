package ru.yandex.practicum.common.exception;

public class UnknownAuthenticationGrantTypeException extends RuntimeException {
    public UnknownAuthenticationGrantTypeException() {
        super("Unknown authentication grant type");
    }
}

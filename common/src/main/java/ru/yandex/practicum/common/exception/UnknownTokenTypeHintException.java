package ru.yandex.practicum.common.exception;

public class UnknownTokenTypeHintException extends RuntimeException {
    public UnknownTokenTypeHintException() {
        super("Unknown token type hint");
    }
}

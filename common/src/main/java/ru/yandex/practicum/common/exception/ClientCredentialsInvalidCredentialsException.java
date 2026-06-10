package ru.yandex.practicum.common.exception;

public class ClientCredentialsInvalidCredentialsException extends RuntimeException {
    public ClientCredentialsInvalidCredentialsException() {
        super("Missing client credentials");
    }
}

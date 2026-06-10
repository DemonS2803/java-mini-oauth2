package ru.yandex.practicum.common.exception;

public class UsernamePasswordInvalidCredentialsException extends RuntimeException {
    public UsernamePasswordInvalidCredentialsException() {
        super("Missing username/password credentials");
    }
}

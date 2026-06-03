package ru.yandex.practicum.oauth.client;

public interface ClientService {

    boolean checkClientSecret(String clientId, String secret);

}

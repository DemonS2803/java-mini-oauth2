package ru.yandex.practicum.common.oauth.dto;

import java.util.List;

public interface AuthenticateRequest {

    String getClientId();
    String getClientSecret();
    List<String> getScopes();
    List<String> getRoles();

}

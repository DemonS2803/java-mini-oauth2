package ru.yandex.practicum.oauth.token.dto;

import java.util.List;

import ru.yandex.practicum.common.oauth.dto.AuthenticateRequest;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatePasswordRequestDto implements AuthenticateRequest {

    private String clientId;
    private String clientSecret;
    private List<String> scopes;
    private List<String> roles;
    private User user;
    private Client client;

}

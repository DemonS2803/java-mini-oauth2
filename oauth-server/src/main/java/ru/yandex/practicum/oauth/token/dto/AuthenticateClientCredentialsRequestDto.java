package ru.yandex.practicum.oauth.token.dto;

import java.util.List;

import ru.yandex.practicum.common.oauth.dto.AuthenticateRequest;
import ru.yandex.practicum.oauth.client.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateClientCredentialsRequestDto implements AuthenticateRequest {

    private String clientId;
    private String clientSecret;
    private List<String> scopes;
    private List<String> roles;
    private Client client;

}

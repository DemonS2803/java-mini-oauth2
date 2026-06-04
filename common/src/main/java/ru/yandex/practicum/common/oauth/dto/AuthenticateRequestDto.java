package ru.yandex.practicum.common.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticateRequestDto {

    private String grantType;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private List<String> scopes;

}

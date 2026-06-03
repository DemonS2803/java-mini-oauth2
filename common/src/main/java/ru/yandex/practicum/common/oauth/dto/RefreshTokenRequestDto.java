package ru.yandex.practicum.common.oauth.dto;

import lombok.Data;

@Data
public class RefreshTokenRequestDto {

    private String grantType;
    private String refreshToken;
    private String clientId;
    private String clientSecret;

}

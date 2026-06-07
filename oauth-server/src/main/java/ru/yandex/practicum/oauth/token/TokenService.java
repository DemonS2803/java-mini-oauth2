package ru.yandex.practicum.oauth.token;

import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;

public interface TokenService {

    AuthenticateResponseDto authenticate(AuthenticatePasswordRequestDto request);

    AuthenticateResponseDto authenticate(AuthenticateClientCredentialsRequestDto request);

    AuthenticateResponseDto refresh(String token);

    void revokeAccessToken(String token);

    void revokeRefreshToken(String token);

    TokenInfoResponseDto getAccessTokenInfo(String token);

    TokenInfoResponseDto getRefreshTokenInfo(String token);

}

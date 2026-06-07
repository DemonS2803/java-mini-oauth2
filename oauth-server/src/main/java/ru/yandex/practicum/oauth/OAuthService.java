package ru.yandex.practicum.oauth;

import ru.yandex.practicum.common.oauth.dto.*;

public interface OAuthService {

    AuthenticateResponseDto authenticate(AuthenticateRequestDto request);

    AuthenticateResponseDto refresh(RefreshTokenRequestDto request);

    void revoke(TokenFetchRequestDto request);

    TokenInfoResponseDto tokenInfo(TokenFetchRequestDto request);

}

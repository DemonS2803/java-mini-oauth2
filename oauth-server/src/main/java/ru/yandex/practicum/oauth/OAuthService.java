package ru.yandex.practicum.oauth;

import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.common.oauth.dto.TokenFetchRequestDto;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;

public interface OAuthService {

    AuthenticateResponseDto authenticate(AuthenticateRequestDto request);

    AuthenticateRequestDto refresh();

    void revoke(TokenFetchRequestDto request);

    TokenInfoResponseDto tokenInfo(TokenFetchRequestDto request);

}

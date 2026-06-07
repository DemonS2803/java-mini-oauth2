package ru.yandex.practicum.oauth.token;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.dto.RefreshTokenRequestDto;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;
import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.oauth.util.RefreshJwt;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;

import java.sql.Ref;
import java.time.LocalDateTime;

public class TokenMapper {

    public static RefreshToken toRefreshToken(AuthenticatePasswordRequestDto request) {
        RefreshToken token = new RefreshToken();
        token.setClient(request.getClient());
        token.setUser(request.getUser());
        return token;
    }

    public static TokenInfoResponseDto toTokenInfoResponse(AccessJwt jwt) {
        LocalDateTime now = LocalDateTime.now();
        TokenInfoResponseDto tokenInfo = new TokenInfoResponseDto();
        tokenInfo.setActive(now.isAfter(jwt.getPayload().getExpiredAt()));
        tokenInfo.setClientId(jwt.getPayload().getClientId());
        tokenInfo.setExpiredAt(jwt.getPayload().getExpiredAt());
        return tokenInfo;
    }

    public static AuthenticatePasswordRequestDto toAuthRequest(RefreshToken refresh) {
        AuthenticatePasswordRequestDto authRequest = new AuthenticatePasswordRequestDto();
        authRequest.setClientId(refresh.getClient().getClientId());
        authRequest.setClientSecret(refresh.getClient().getSecretHash());
        authRequest.setScopes(refresh.getScopes());
        authRequest.setClient(refresh.getClient());
        return authRequest;
    }

}

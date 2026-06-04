package ru.yandex.practicum.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.common.oauth.dto.TokenFetchRequestDto;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;
import ru.yandex.practicum.oauth.client.ClientService;
import ru.yandex.practicum.oauth.token.TokenService;
import ru.yandex.practicum.oauth.user.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UserService userService;
    private final ClientService clientService;
    private final TokenService tokenService;

    @Override
    public AuthenticateResponseDto authenticate(AuthenticateRequestDto request) {
        return null;
    }

    @Override
    public AuthenticateRequestDto refresh() {
        return null;
    }

    @Override
    public void revoke(TokenFetchRequestDto request) {

    }

    @Override
    public TokenInfoResponseDto tokenInfo(TokenFetchRequestDto request) {
        return null;
    }

    protected void checkUsernamePasswordCredentials(String username, String password) {

    }

    protected void checkClientCredentials(String clientId, String clientSecret) {

    }
}

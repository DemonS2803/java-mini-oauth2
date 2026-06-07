package ru.yandex.practicum.oauth;

import java.util.List;

import ru.yandex.practicum.common.exception.*;
import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.common.oauth.permissions.SimpleRBAC;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.client.ClientService;
import ru.yandex.practicum.oauth.token.TokenService;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;
import ru.yandex.practicum.oauth.user.User;
import ru.yandex.practicum.oauth.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UserService userService;
    private final ClientService clientService;
    private final TokenService tokenService;

    @Override
    public AuthenticateResponseDto authenticate(AuthenticateRequestDto request) {
        log.info("Incoming authenticate request: {}", request);
        return switch (request.getGrantType()) {
            case "password" -> authenticatePassword(request);
            case "client_credentials" -> authenticateClientCredentials(request);
            default -> {
                log.error("Unknown authentication grant type {}", request.getGrantType());
                throw new UnknownAuthenticationGrantTypeException();
            }
        };

    }

    private AuthenticateResponseDto authenticateClientCredentials(AuthenticateRequestDto request) {
        checkClientCredentials(request.getClientId(), request.getClientSecret());
        log.info("Authenticate request for client {}", request.getClientId());

        AuthenticateClientCredentialsRequestDto clientCredentialsRequest = createClientCredentialsRequest(request);
        return tokenService.authenticate(clientCredentialsRequest);
    }

    private AuthenticateResponseDto authenticatePassword(AuthenticateRequestDto request) {
        checkClientCredentials(request.getClientId(), request.getClientSecret());
        checkUsernamePasswordCredentials(request.getUsername(), request.getPassword());
        checkPermissions(request.getUsername(), request.getScopes());
        log.info("Authenticate request for user {} by client {}", request.getUsername(), request.getClientId());

        AuthenticatePasswordRequestDto passwordRequest = createPasswordRequest(request);
        return tokenService.authenticate(passwordRequest);
    }

    @Override
    public AuthenticateResponseDto refresh(RefreshTokenRequestDto request) {
        checkClientCredentials(request.getClientId(), request.getClientSecret());
        log.info("Refresh token {}", request.getRefreshToken());
        return tokenService.refresh(request.getRefreshToken());
    }

    @Override
    public void revoke(TokenFetchRequestDto request) {
        log.info("Revoke token request: {}", request);
        switch (request.getTokenTypeHint()) {
            case "access_token" -> tokenService.revokeAccessToken(request.getToken());
            case "refresh_token" -> tokenService.revokeRefreshToken(request.getToken());
            default -> {
                log.warn("Couldn't revoke token by hint: {}", request.getTokenTypeHint());
                throw new UnknownTokenTypeHintException();
            }
        }
    }

    @Override
    public TokenInfoResponseDto tokenInfo(TokenFetchRequestDto request) {
        log.info("Introspect token {}", request);
        return switch (request.getTokenTypeHint()) {
            case "access_token" -> tokenService.getAccessTokenInfo(request.getToken());
            case "refresh_token" -> tokenService.getRefreshTokenInfo(request.getToken());
            default -> {
                log.warn("Couldn't get token info by hint: {}", request.getTokenTypeHint());
                throw new UnknownTokenTypeHintException();
            }
        };
    }

    protected void checkUsernamePasswordCredentials(String username, String password) {
        if (!userService.checkUserPassword(username, password)) {
            throw new UsernamePasswordInvalidCredentialsException();
        }
    }

    protected void checkClientCredentials(String clientId, String clientSecret) {
        if (!clientService.checkClientSecret(clientId, clientSecret)) {
            throw new ClientCredentialsInvalidCredentialsException();
        }
    }

    private AuthenticatePasswordRequestDto createPasswordRequest(AuthenticateRequestDto request) {
        User user = userService.getUserByUsername(request.getUsername());
        Client client = clientService.getClientById(request.getClientId());

        return AuthenticatePasswordRequestDto.builder()
                .user(user)
                .scopes(request.getScopes())
                .roles(user.getRoles())
                .client(client)
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .build();
    }

    private AuthenticateClientCredentialsRequestDto createClientCredentialsRequest(AuthenticateRequestDto request) {
        Client client = clientService.getClientById(request.getClientId());

        return AuthenticateClientCredentialsRequestDto.builder()
                .scopes(request.getScopes())
                .client(client)
                .roles(SimpleRBAC.getMinimalRolesList(request.getScopes()))
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .build();
    }

    private void checkPermissions(String username, List<String> requestedScopes) {
        User user = userService.getUserByUsername(username);
        if (!SimpleRBAC.isValidPermissions(user.getRoles(), requestedScopes)) {
            throw new NotPermittedException("Requested scopes not permitted for user " + username);
        }
    }
}

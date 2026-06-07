package ru.yandex.practicum.oauth;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.common.exception.UnknownAuthenticationGrantTypeException;
import ru.yandex.practicum.common.exception.UnknownTokenTypeHintException;
import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.client.ClientRepository;
import ru.yandex.practicum.oauth.client.ClientService;
import ru.yandex.practicum.oauth.common.PasswordUtil;
import ru.yandex.practicum.oauth.token.TokenService;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.user.User;
import ru.yandex.practicum.oauth.user.UserRepository;
import ru.yandex.practicum.oauth.user.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.common.testutil.TestStubs.*;

@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AuthApp.class)
@Transactional
public class OAuthServiceTest {

    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordUtil passwordUtil;

    private Client client;
    private User user;

    @BeforeEach
    void setup() {
        client = createClient();
        user = createUser();
    }


    @Test
    void testAuthenticatePassword_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        assertNotNull(authenticate);
        assertNotNull(authenticate.getAccessToken());
        assertNotNull(authenticate.getRefreshToken());
    }

    @Test
    void testAuthenticateClientCredentials_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getClientCredentialAuthRequest());
        assertNotNull(authenticate);
        assertNotNull(authenticate.getAccessToken());
        assertNull(authenticate.getRefreshToken());
    }

    @Test
    void testAuthenticateClientCredentials_unknownGrantType_shouldFail() {
        assertThrows(UnknownAuthenticationGrantTypeException.class, () -> {
            AuthenticateRequestDto request = getClientCredentialAuthRequest();
            request.setGrantType("test_grant");
            AuthenticateResponseDto authenticate = oAuthService.authenticate(request);
        });
    }

    @Test
    void testRefreshToken_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        AuthenticateResponseDto refresh = oAuthService.refresh(getRefreshRequestDto(authenticate.getRefreshToken()));

        assertNotNull(refresh);
        assertNotNull(refresh.getAccessToken());
        assertNotNull(refresh.getRefreshToken());
    }

    @Test
    void testRevokeAccessToken_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        TokenFetchRequestDto request = new TokenFetchRequestDto(authenticate.getAccessToken(), "access_token");
        oAuthService.revoke(request);

        TokenInfoResponseDto tokenInfo = oAuthService.tokenInfo(request);
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRevokeRefreshToken_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        TokenFetchRequestDto request = new TokenFetchRequestDto(authenticate.getRefreshToken(), "refresh_token");
        oAuthService.revoke(request);

        TokenInfoResponseDto tokenInfo = oAuthService.tokenInfo(request);
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRevokeAccessToken_unknownHint_shouldFail() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        TokenFetchRequestDto request = new TokenFetchRequestDto(authenticate.getAccessToken(), "test_token");

        assertThrows(UnknownTokenTypeHintException.class, () -> {
            oAuthService.revoke(request);
        });

    }

    private RefreshTokenRequestDto getRefreshRequestDto(String refreshToken) {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setGrantType("refresh_token");
        refreshRequest.setClientId(VALID_CLIENT_ID);
        refreshRequest.setClientSecret(VALID_CLIENT_SECRET);
        refreshRequest.setRefreshToken(refreshToken);
        return refreshRequest;
    }



    private Client createClient() {
        Client c = new Client();
        c.setClientId(VALID_CLIENT_ID);
        c.setSecretHash(VALID_CLIENT_SECRET_HASH);
        c.setInfo("test");
        clientRepository.save(c);
        return c;
    }

    private User createUser() {
        User u = new User();
        u.setUsername(VALID_USERNAME);
        u.setPasswordHash(VALID_PASSWORD_HASH);
        u.setInfo("test_info");
        u.setRoles(JWT_ROLES);
        userRepository.save(u);
        return u;
    }
}

package ru.yandex.practicum.oauth;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.oauth.client.ClientService;
import ru.yandex.practicum.oauth.token.TokenService;
import ru.yandex.practicum.oauth.user.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.yandex.practicum.common.testutil.TestStubs.*;
import static ru.yandex.practicum.common.testutil.TestStubs.VALID_CLIENT_SECRET;

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


    @Test
    void testAuthenticatePassword_shouldSuccess() {
        AuthenticateResponseDto authenticate = oAuthService.authenticate(getPasswordAuthRequest());
        assertNotNull(authenticate);
        assertNotNull(authenticate.getAccessToken());
        assertNotNull(authenticate.getRefreshToken());
    }

    @Test
    void testAuthenticateClientCredentials_shouldSuccess() {

    }

    private AuthenticateRequestDto getPasswordAuthRequest() {
        return AuthenticateRequestDto.builder()
                .grantType("password")
                .scopes(JWT_PAYMENT_SCOPES)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();
    }

    private AuthenticateRequestDto getClientCredentialAuthRequest() {
        return AuthenticateRequestDto.builder()
                .grantType("client_credentials")
                .scopes(JWT_PAYMENT_SCOPES)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .build();
    }
}

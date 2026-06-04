package ru.yandex.practicum.oauth.token;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.common.exception.JwtEncodeException;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.oauth.AuthApp;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.common.testutil.TestStubs.*;

@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AuthApp.class)
@Transactional
public class TokerServiceTest {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private RefreshIndexRepository refreshIndexRepository;
    @Autowired
    private RevocationRepository revocationRepository;

    @Test
    void testAuthenticatePassword_shouldSuccess() {
        AuthenticateResponseDto authenticate = tokenService.authenticate(getPasswordAuthRequest());
        assertNotNull(authenticate);
        assertNotNull(authenticate.getAccessToken());
        assertNotNull(authenticate.getAccessTtlSec());
        assertNotNull(authenticate.getRefreshToken());
        assertNotNull(authenticate.getRefreshTtlDays());
    }

    @Test
    void testAuthenticateClientCredentials_shouldSuccess() {
        AuthenticateResponseDto authenticate = tokenService.authenticate(getClientCredentialsAuthRequest());
        assertNotNull(authenticate);
        assertNotNull(authenticate.getAccessToken());
        assertNotNull(authenticate.getAccessTtlSec());
        assertNull(authenticate.getRefreshToken());
        assertNull(authenticate.getRefreshTtlDays());
    }

    @Test
    void testBuildResponseNullClientID_shouldFail() {
        AuthenticateClientCredentialsRequestDto request = getClientCredentialsAuthRequest();
        request.setClientId(null);
        assertThrows(JwtEncodeException.class, () -> {
            tokenService.authenticate(request);
        });
    }

    @Test
    void testGetTokenInfo_shouldSuccess() {
        AuthenticateResponseDto authenticate = tokenService.authenticate(getPasswordAuthRequest());
        assertNotNull(authenticate);
        TokenInfoResponseDto tokenInfo = tokenService.getAccessTokenInfo(authenticate.getAccessToken());
        assertNotNull(tokenInfo);
        assertEquals(authenticate.getIssuer(), tokenInfo.)
    }

//    @Test
//    void testAuthenticateClientCredentials_shouldSuccess() {
//        AuthenticateResponseDto authenticate = tokenService.authenticate(getClientCredentialsAuthRequest());
//        assertNotNull(authenticate);
//        assertNotNull(authenticate.getAccessToken());
//        assertNotNull(authenticate.getRefreshToken());
//    }

    private AuthenticatePasswordRequestDto getPasswordAuthRequest() {
        return AuthenticatePasswordRequestDto.builder()
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .build();
    }

    private AuthenticateClientCredentialsRequestDto getClientCredentialsAuthRequest() {
        return AuthenticateClientCredentialsRequestDto.builder()
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .build();
    }

}

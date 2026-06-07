package ru.yandex.practicum.oauth.token;

import jakarta.transaction.Transactional;

import ru.yandex.practicum.common.exception.RefreshTokenInvalidException;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;
import ru.yandex.practicum.common.oauth.util.JwtUtil;
import ru.yandex.practicum.common.oauth.util.RefreshJwt;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.client.ClientRepository;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.oauth.AuthApp;
import ru.yandex.practicum.oauth.user.User;
import ru.yandex.practicum.oauth.user.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Value("${config.oauth.secret}")
    private String oauthSecret;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private RefreshIndexRepository refreshIndexRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;

    private Client client;
    private User user;

    @BeforeEach
    void setup() {
        client = createClient();
        user = createUser();
    }

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
        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.authenticate(request);
        });
    }

    @Test
    void testGetTokenInfo_shouldSuccess() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        assertNotNull(authenticate);
        TokenInfoResponseDto tokenInfo = tokenService.getAccessTokenInfo(authenticate.getAccessToken());
        assertNotNull(tokenInfo);
        assertTrue(tokenInfo.isActive());
        assertEquals(request.getClientId(), tokenInfo.getClientId());
    }

    @Test
    void testRevokeAccessToken_shouldSuccess() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        // Check is active
        TokenInfoResponseDto tokenInfo = tokenService.getAccessTokenInfo(authenticate.getAccessToken());
        assertTrue(tokenInfo.isActive());

        // Revoke
        tokenService.revokeAccessToken(authenticate.getAccessToken());

        // Check that token is dead
        tokenInfo = tokenService.getAccessTokenInfo(authenticate.getAccessToken());
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRevokeRefreshToken_shouldSuccess() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        request.setClient(clientRepository.findClientByClientId(VALID_CLIENT_ID).get());
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        // Check is active
        TokenInfoResponseDto tokenInfo = tokenService.getRefreshTokenInfo(authenticate.getRefreshToken());
        assertTrue(tokenInfo.isActive());

        // Revoke
        tokenService.revokeRefreshToken(authenticate.getRefreshToken());

        // Check that token is dead
        tokenInfo = tokenService.getRefreshTokenInfo(authenticate.getRefreshToken());
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRefreshToken_issueValidTokens_shouldSuccess() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        AuthenticateResponseDto refresh = tokenService.refresh(authenticate.getRefreshToken());
        assertNotNull(refresh);
        assertNotNull(refresh.getAccessToken());
        assertNotNull(refresh.getRefreshToken());

        TokenInfoResponseDto tokenInfo;
        tokenInfo = tokenService.getRefreshTokenInfo(refresh.getRefreshToken());
        assertTrue(tokenInfo.isActive());
        tokenInfo = tokenService.getAccessTokenInfo(refresh.getAccessToken());
        assertTrue(tokenInfo.isActive());
    }

    @Test
    void testRefreshToken_invalidateOldTokens_shouldSuccess() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        AuthenticateResponseDto refresh = tokenService.refresh(authenticate.getRefreshToken());

        TokenInfoResponseDto tokenInfo;
        tokenInfo = tokenService.getRefreshTokenInfo(authenticate.getRefreshToken());
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRefreshToken_refreshRevoked_shouldFail() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        // revoke
        tokenService.revokeRefreshToken(authenticate.getRefreshToken());
        // refresh revoked should
        assertThrows(RefreshTokenInvalidException.class, () -> {
            AuthenticateResponseDto refresh = tokenService.refresh(authenticate.getRefreshToken());
        });
    }

    @Test
    void testRefreshToken_refreshExpired_shouldFail() {
        AuthenticatePasswordRequestDto request = getPasswordAuthRequest();
        AuthenticateResponseDto authenticate = tokenService.authenticate(request);
        // force expire
        RefreshJwt jwt = JwtUtil.decodeRefreshAndVerify(authenticate.getRefreshToken(), oauthSecret);
        RefreshToken dbToken = refreshIndexRepository.findRefreshTokenById(jwt.getPayload().getRefreshId()).get();
        dbToken.setExpiredAt(DAY_AGO);
        refreshIndexRepository.save(dbToken);
        // refresh revoked should
        assertThrows(RefreshTokenInvalidException.class, () -> {
            AuthenticateResponseDto refresh = tokenService.refresh(authenticate.getRefreshToken());
        });
    }


    private Client createClient() {
        Client c = new Client();
        c.setClientId(VALID_CLIENT_ID);
        c.setSecretHash(VALID_CLIENT_SECRET);
        c.setInfo("test");
        clientRepository.save(c);
        return c;
    }

    private User createUser() {
        User u = new User();
        u.setUsername(VALID_USERNAME);
        u.setPasswordHash(VALID_PASSWORD);
        u.setInfo("test_info");
        userRepository.save(u);
        return u;
    }

    private AuthenticatePasswordRequestDto getPasswordAuthRequest() {
        return AuthenticatePasswordRequestDto.builder()
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .client(clientRepository.findClientByClientId(VALID_CLIENT_ID).get())
                .user(userRepository.findUserByUsername(VALID_USERNAME).get())
                .build();
    }

    private AuthenticateClientCredentialsRequestDto getClientCredentialsAuthRequest() {
        return AuthenticateClientCredentialsRequestDto.builder()
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .build();
    }

}

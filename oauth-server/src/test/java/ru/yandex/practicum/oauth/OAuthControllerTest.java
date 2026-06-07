package ru.yandex.practicum.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.common.web.HttpConstants;
import ru.yandex.practicum.oauth.client.Client;
import ru.yandex.practicum.oauth.client.ClientRepository;
import ru.yandex.practicum.oauth.user.User;
import ru.yandex.practicum.oauth.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.common.testutil.TestStubs.*;

@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = AuthApp.class)
@Transactional
@AutoConfigureMockMvc
public class OAuthControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;

    private User user;
    private Client client;

    @BeforeEach
    void setup() {
        user = createUser();
        client = createClient();
    }

    @Test
    void testAuthenticatePassword_success200() throws Exception {
        AuthenticateResponseDto auth = authenticate(getPasswordAuthRequest());
        assertNotNull(auth);
        assertNotNull(auth.getAccessToken());
        assertNotNull(auth.getRefreshToken());
    }

    @Test
    void testAuthenticateClientCredentials_success200() throws Exception {
        AuthenticateResponseDto auth = authenticate(getClientCredentialAuthRequest());
        assertNotNull(auth);
        assertNotNull(auth.getAccessToken());
        assertNull(auth.getRefreshToken());
    }

    @Test
    void testAuthenticatePassword_invalidCredentials_unauthorized401() throws Exception {
        AuthenticateRequestDto request = getPasswordAuthRequest();
        request.setPassword("invalid");
        ResultActions result = authRequest(request);
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticatePassword_userNotFound_notFound404() throws Exception {
        AuthenticateRequestDto request = getPasswordAuthRequest();
        request.setUsername("unknown");
        ResultActions result = authRequest(request);
        result.andExpect(status().isNotFound());
    }

    @Test
    void testAuthenticatePassword_scopeNotPermitted_notPermitted403() throws Exception {
        String username = "testUser";
        User user2 = createUser();
        user2.setUsername(username);
        user2.setRoles(List.of(JWT_READ_PAYMENT_SCOPE));
        userRepository.save(user2);


        AuthenticateRequestDto request = getPasswordAuthRequest();
        request.setUsername(username);
        request.setScopes(List.of("payment:write"));
        ResultActions result = authRequest(request);
        result.andExpect(status().isForbidden());
    }

    @Test
    void testAuthenticateClientCredentials_invalidCredentials_unauthorized401() throws Exception {
        AuthenticateRequestDto request = getClientCredentialAuthRequest();
        request.setClientSecret("invalid");
        ResultActions result = authRequest(request);
        result.andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthenticatePassword_unknownGrantType_badRequest400() throws Exception {
        AuthenticateRequestDto request = getClientCredentialAuthRequest();
        request.setGrantType("test_grant");
        ResultActions result = authRequest(request);
        result.andExpect(status().isBadRequest());
    }

    @Test
    void testRefresh_success200() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);
        AuthenticateResponseDto refresh = refresh(mapToRefreshRequest(auth, authRequest));

        assertNotNull(refresh);
        assertNotNull(refresh.getAccessToken());
        assertNotNull(refresh.getRefreshToken());
    }

    @Test
    void testRefresh_invalidCredentials_unauthorized401() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);
        authRequest.setClientSecret("invalid");
        ResultActions refresh = refreshRequest(mapToRefreshRequest(auth, authRequest));

        refresh.andExpect(status().isUnauthorized());
    }

    @Test
    void testRefresh_useOld_conflict409() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);
        AuthenticateResponseDto refresh = refresh(mapToRefreshRequest(auth, authRequest));
        ResultActions secondRefresh = refreshRequest(mapToRefreshRequest(auth, authRequest));

        secondRefresh.andExpect(status().isConflict());
    }

    @Test
    void testRefresh_useRevoked_conflict409() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);
        revoke(new TokenFetchRequestDto(auth.getRefreshToken(), "refresh_token"));
        ResultActions refreshRevoked = refreshRequest(mapToRefreshRequest(auth, authRequest));

        refreshRevoked.andExpect(status().isConflict());
    }


    @Test
    void testRevokeAccess_success200() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);

        TokenFetchRequestDto fetchToken = new TokenFetchRequestDto(auth.getAccessToken(), "access_token");
        revoke(fetchToken);

        TokenInfoResponseDto tokenInfo = tokenInfo(fetchToken);
        assertNotNull(tokenInfo);
        assertFalse(tokenInfo.isActive());
    }

    @Test
    void testRevokeRefresh_success200() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);

        TokenFetchRequestDto fetchToken = new TokenFetchRequestDto(auth.getRefreshToken(), "refresh_token");
        revoke(fetchToken);

        TokenInfoResponseDto tokenInfo = tokenInfo(fetchToken);
        assertNotNull(tokenInfo);
        assertFalse(tokenInfo.isActive());
    }


    @Test
    void testRevokeRefresh_invalidHint_badRequest400() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);

        TokenFetchRequestDto fetchToken = new TokenFetchRequestDto(auth.getRefreshToken(), "test_token");
        ResultActions result = revokeRequest(fetchToken);
        result.andExpect(status().isBadRequest());
    }

    @Test
    void testTokenInfo_failJwtDecode_badRequest400() throws Exception {
        AuthenticateRequestDto authRequest = getPasswordAuthRequest();
        AuthenticateResponseDto auth = authenticate(authRequest);

        String token = auth.getRefreshToken();
        token = token.replace("ey", "la");
        TokenFetchRequestDto fetchToken = new TokenFetchRequestDto(token, "refresh_token");
        ResultActions result = tokenInfoRequest(fetchToken);
        result.andExpect(status().isUnauthorized());
    }

    private AuthenticateResponseDto authenticate(AuthenticateRequestDto request) throws Exception {
        ResultActions result = authRequest(request);
        result.andExpect(status().is2xxSuccessful());
        String json = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(json, AuthenticateResponseDto.class);
    }

    private ResultActions authRequest(AuthenticateRequestDto request) throws Exception {
        return mvc.perform(post(HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_TOKEN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    private AuthenticateResponseDto refresh(RefreshTokenRequestDto request) throws Exception {
        ResultActions result = refreshRequest(request);
        result.andExpect(status().is2xxSuccessful());
        String json = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(json, AuthenticateResponseDto.class);
    }

    private RefreshTokenRequestDto mapToRefreshRequest(AuthenticateResponseDto auth, AuthenticateRequestDto authRequest) {
        RefreshTokenRequestDto refreshRequest = new RefreshTokenRequestDto();
        refreshRequest.setClientId(authRequest.getClientId());
        refreshRequest.setClientSecret(authRequest.getClientSecret());
        refreshRequest.setRefreshToken(auth.getRefreshToken());
        refreshRequest.setGrantType("refresh_token");
        return refreshRequest;
    }

    private ResultActions refreshRequest(RefreshTokenRequestDto request) throws Exception {
        return mvc.perform(post(HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_REFRESH_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    private TokenInfoResponseDto tokenInfo(TokenFetchRequestDto request) throws Exception {
        ResultActions result = tokenInfoRequest(request);
        result.andExpect(status().is2xxSuccessful());
        String json = result.andReturn().getResponse().getContentAsString();
        return mapper.readValue(json, TokenInfoResponseDto.class);
    }

    private ResultActions tokenInfoRequest(TokenFetchRequestDto request) throws Exception {
        return mvc.perform(get(HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_INTROSPECT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
    }

    private void revoke(TokenFetchRequestDto request) throws Exception {
        ResultActions result = revokeRequest(request);
        result.andExpect(status().is2xxSuccessful());
    }

    private ResultActions revokeRequest(TokenFetchRequestDto request) throws Exception {
        return mvc.perform(post(HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_REVOKE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
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

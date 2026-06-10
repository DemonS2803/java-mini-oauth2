package ru.yandex.practicum.rs.resource;

import java.util.List;

import jakarta.transaction.Transactional;

import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.oauth.util.JwtUtil;
import ru.yandex.practicum.common.web.HttpConstants;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.rs.ResourceApp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.common.testutil.TestStubs.*;

@Slf4j
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ResourceApp.class)
@Transactional
@AutoConfigureMockMvc
public class ResourceControllerTest {

    @Autowired
    private MockMvc mvc;

    // public secret is not used for encoding,
    // but in learning case we could do this
    @Value("${config.oauth.public_secret}")
    private String secret;
    @Value("${config.oauth.time.skew.sec}")
    private Long oauthTokenSkewSec;
    @Value("${config.oauth.active.aud}")
    private String activeAudience;

    @Test
    void testRedirect_noToken_redirect300() throws Exception {
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is3xxRedirection());
    }

    @Test
    void testGetResource_withToken_success200() throws Exception {
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                        .header(HttpHeaders.AUTHORIZATION, getJWT())
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetResource_withBearerToken_success200() throws Exception {
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getJWT())
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetResource_noScope_forbidden403() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setAudience(activeAudience);
        jwt.getPayload().setScopes(List.of());
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }

    @Test
    void testGetResource_nullPayload_forbidden403() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.setPayload(null);
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isBadRequest());
    }

    @Test
    void testGetResource_expiredToken_redirect300() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setExpiredAt(DAY_AGO);
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is3xxRedirection());
    }

    @Test
    void testGetResource_expirationSkewBorderCase_success200() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setAudience(activeAudience);
        jwt.getPayload().setExpiredAt(NOW.minusSeconds(oauthTokenSkewSec - 1));
        ResultActions result = mvc.perform(get(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetResource_expirationSkewBorderCase_redirect300() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setExpiredAt(NOW.minusSeconds(oauthTokenSkewSec + 1));
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is3xxRedirection());
    }

    @Test
    void testGetResource_invalidAudience_redirect300() throws Exception {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setAudience("invalid");
        ResultActions result = mvc.perform(post(HttpConstants.PAYMENTS_PATH)
                .header(HttpHeaders.AUTHORIZATION, JwtUtil.encode(jwt, secret))
                .contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().is3xxRedirection());
    }

    private String getJWT() {
        AccessJwt jwt = getAccessJwt();
        jwt.getPayload().setAudience(activeAudience);
        return JwtUtil.encode(jwt, secret);
    }

}

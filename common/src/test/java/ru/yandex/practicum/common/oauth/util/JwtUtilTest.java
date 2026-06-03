package ru.yandex.practicum.common.oauth.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.common.oauth.enums.TokenType;
import ru.yandex.practicum.common.testutil.TestStubs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.common.testutil.TestStubs.*;


@Slf4j
public class JwtUtilTest {

    protected static final String SECRET = "super-secret";

    @Test
    void testEncode_shouldSuccessEncode() throws Exception {
        Jwt token = getJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        assertNotNull(encodedToken);
        assertEquals(3, encodedToken.split("\\.").length);
    }

    @Test
    void testEncode_shouldSuccessEncodeDecode() throws Exception {
        Jwt token = getJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        Jwt decodedToken = JwtUtil.decodeAndVerify(encodedToken, VALID_CLIENT_SECRET);
        assertEquals(token.getHeader(), decodedToken.getHeader());
        assertEquals(token.getPayload(), decodedToken.getPayload());
        assertEquals(token.getHeader().getAlgorithm(), decodedToken.getHeader().getAlgorithm());
        assertEquals(token.getPayload().getScopes(), decodedToken.getPayload().getScopes());
    }

    private Jwt getJwt() {
        return Jwt.builder()
                .header(getValidHeader())
                .payload(getValidPayload())
                .build();
    }

    private JwtHeader getValidHeader() {
        return JwtHeader.builder()
                .algorithm(JwtUtil.ALGORITHM)
                .type(TokenType.AT)
                .build();
    }

    private JwtPayload getValidPayload() {
        return JwtPayload.builder()
                .clientId(VALID_CLIENT_ID)
                .issuer(JWT_TOKEN_ISSUER)
                .audience(JWT_VALID_AUDIENCE)
                .sub(JWT_VALID_SUB)
                .roles(JWT_ROLES)
                .expiredAt(DAY_NEXT)
                .issuedAt(DAY_AGO)
                .scopes(List.of(JWT_READ_PAYMENT_SCOPE, JWT_WRITE_PAYMENT_SCOPE))
                .build();
    }
}

package ru.yandex.practicum.common.oauth.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.common.oauth.enums.TokenType;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.common.testutil.TestStubs.*;


@Slf4j
public class JwtUtilTest {

    protected static final String SECRET = "super-secret";

    @Test
    void testEncodeAccess_shouldSuccessEncode() throws Exception {
        AccessJwt token = getAccessJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        assertNotNull(encodedToken);
        assertEquals(3, encodedToken.split("\\.").length);
    }

    @Test
    void testEncodeAccess_shouldSuccessEncodeDecode() throws Exception {
        AccessJwt token = getAccessJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        AccessJwt decodedToken = JwtUtil.decodeAccessAndVerify(encodedToken, VALID_CLIENT_SECRET);
        assertEquals(token.getHeader(), decodedToken.getHeader());
        assertEquals(token.getPayload(), decodedToken.getPayload());
        assertEquals(TokenType.AT, decodedToken.getHeader().getType());
        assertEquals(token.getPayload().getScopes(), decodedToken.getPayload().getScopes());
    }

    @Test
    void testEncodeRefresh_shouldSuccessEncode() throws Exception {
        RefreshJwt token = getRefreshJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        assertNotNull(encodedToken);
        assertEquals(3, encodedToken.split("\\.").length);
    }

    @Test
    void testEncodeRefresh_shouldSuccessEncodeDecode() throws Exception {
        RefreshJwt token = getRefreshJwt();
        String encodedToken = JwtUtil.encode(token, VALID_CLIENT_SECRET);

        log.info(encodedToken);
        RefreshJwt decodedToken = JwtUtil.decodeRefreshAndVerify(encodedToken, VALID_CLIENT_SECRET);
        assertEquals(token.getHeader(), decodedToken.getHeader());
        assertEquals(token.getPayload(), decodedToken.getPayload());
        assertEquals(TokenType.RT, decodedToken.getHeader().getType());
    }

    private AccessJwt getAccessJwt() {
        JwtHeader header = getValidHeader();
        header.setType(TokenType.AT);
        return AccessJwt.builder()
                .header(header)
                .payload(getValidAccessPayload())
                .build();
    }

    private RefreshJwt getRefreshJwt() {
        JwtHeader header = getValidHeader();
        header.setType(TokenType.RT);
        return RefreshJwt.builder()
                .header(header)
                .payload(getValidRefreshPayload())
                .build();
    }

    private JwtHeader getValidHeader() {
        return JwtHeader.builder()
                .algorithm(JwtUtil.ALGORITHM)
                .build();
    }

    private AccessJwtPayload getValidAccessPayload() {
        return AccessJwtPayload.builder()
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

    private RefreshJwtPayload getValidRefreshPayload() {
        return RefreshJwtPayload.builder()
                .refreshId(UUID.randomUUID())
                .build();
    }
}

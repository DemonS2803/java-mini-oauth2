package ru.yandex.practicum.common.testutil;

import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.enums.TokenType;
import ru.yandex.practicum.common.oauth.permissions.SimpleRBAC;
import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.oauth.util.AccessJwtPayload;
import ru.yandex.practicum.common.oauth.util.JwtHeader;
import ru.yandex.practicum.common.oauth.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestStubs {

    // Time
    public static final LocalDateTime NOW = LocalDateTime.now();
    public static final LocalDateTime DAY_AGO = NOW.minusDays(1);
    public static final LocalDateTime WEEK_AGO = NOW.minusWeeks(1);
    public static final LocalDateTime MONTH_AGO = NOW.minusMonths(1);
    public static final LocalDateTime DAY_NEXT = NOW.plusDays(1);
    public static final LocalDateTime WEEK_NEXT = NOW.plusWeeks(1);
    public static final LocalDateTime MONTH_NEXT = NOW.plusMonths(1);


    // User


    // Client
    public static final String VALID_CLIENT_ID = "test client";
    public static final String VALID_CLIENT_SECRET = "test secret";
    public static final String VALID_CLIENT_SECRET_HASH = "$2a$12$gh99z.eQRvyJaV4Ay/D69OjeEtNThLOZu5oCYoUrUfzcP9fePSm2W";
    public static final String VALID_USERNAME = "test_username";
    public static final String VALID_PASSWORD = "test_password";
    public static final String VALID_PASSWORD_HASH = "$2a$12$xvyBUZdxI6qwph5FncLXSO1yyOk14w0JRID7okJO6ywwquT0EarsW";

    // JWT
    public static final String JWT_TOKEN_ISSUER = "oauth-server";
    public static final String JWT_VALID_ACCESS_JTI = "jti";
    public static final String JWT_VALID_AUDIENCE = "aud1";
    public static final String JWT_VALID_SUB = "sub1";
    public static final String JWT_READ_PAYMENT_SCOPE = "payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.READ_SCOPE;
    public static final String JWT_WRITE_PAYMENT_SCOPE = "payments" + SimpleRBAC.SEPARATOR + SimpleRBAC.EDIT_SCOPE;
    public static final List<String> JWT_PAYMENT_SCOPES = List.of(JWT_READ_PAYMENT_SCOPE, JWT_WRITE_PAYMENT_SCOPE);
    public static final List<String> JWT_ROLES = List.of(SimpleRBAC.VIEWER_ROLE, SimpleRBAC.EDITOR_ROLE);



    public static AuthenticateRequestDto getPasswordAuthRequest() {
        return AuthenticateRequestDto.builder()
                .grantType("password")
                .scopes(JWT_PAYMENT_SCOPES)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .username(VALID_USERNAME)
                .password(VALID_PASSWORD)
                .build();
    }

    public static AuthenticateRequestDto getClientCredentialAuthRequest() {
        return AuthenticateRequestDto.builder()
                .grantType("client_credentials")
                .scopes(JWT_PAYMENT_SCOPES)
                .clientId(VALID_CLIENT_ID)
                .clientSecret(VALID_CLIENT_SECRET)
                .build();
    }

    public static AccessJwt getAccessJwt() {
        LocalDateTime now = LocalDateTime.now();
        AccessJwtPayload payload = AccessJwtPayload.builder()
                .issuer(VALID_CLIENT_ID)
                .roles(JWT_ROLES)
                .scopes(JWT_PAYMENT_SCOPES)
                .clientId(VALID_CLIENT_ID)
                .sub(VALID_CLIENT_ID)
                .audience(VALID_CLIENT_ID)
                .expiredAt(now.plusSeconds(6000))
                .tokenId(UUID.randomUUID())
                .issuedAt(now)
                .build();
        JwtHeader header = JwtHeader.builder()
                .algorithm(JwtUtil.ALGORITHM)
                .type(TokenType.AT)
                .build();
        return AccessJwt.builder()
                .header(header)
                .payload(payload)
                .build();
    }

}

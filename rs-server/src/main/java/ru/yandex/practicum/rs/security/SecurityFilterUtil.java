package ru.yandex.practicum.rs.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.common.exception.JwtDecodeException;
import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.oauth.util.AccessJwtPayload;
import ru.yandex.practicum.common.oauth.util.JwtUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Component
class SecurityFilterUtil {

    @Value("${config.oauth.public_secret}")
    private String oauthPublicSecret;


    protected AccessJwt getAccessTokenFromRequest(HttpServletRequest request) {
        String token = getAuthorizationToken(request);
        try {
            return JwtUtil.decodeAccessAndVerify(token, oauthPublicSecret);
        } catch (Exception e) {
            throw new JwtDecodeException("Failed to decode incoming JWT: " + e.getMessage());
        }
    }

    protected String getAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token.startsWith("Bearer")) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }

    protected boolean validateAccessToken(AccessJwt jwt) {
        if (jwt == null || jwt.getPayload() == null) {
            throw new JwtDecodeException("JWT payload id null");
        }
        LocalDateTime now = LocalDateTime.now();
        if (jwt.getPayload().getExpiredAt().isBefore(now)) {
            throw new JwtDecodeException("JWT is expired at {}" + jwt.getPayload().getExpiredAt());
        }
        return true;
    }

}

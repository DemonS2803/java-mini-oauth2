package ru.yandex.practicum.rs.security;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.oauth.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class SecurityFilterUtil {

    @Value("${config.oauth.public_secret}")
    private String oauthPublicSecret;
    @Value("${config.oauth.time.skew.sec}")
    private Long oauthTokenSkewSec;
    @Value("${config.oauth.active.aud}")
    private String activeServerAud;


    protected AccessJwt getAccessTokenFromRequest(HttpServletRequest request) {
        String token = getAuthorizationToken(request);
        try {
            return JwtUtil.decodeAccessAndVerify(token, oauthPublicSecret);
        } catch (Exception e) {
            log.error("Failed to decode incoming JWT: " + e.getMessage());
            return null;
        }
    }

    protected String getAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            return null;
        }
        if (token.startsWith("Bearer")) {
            token = token.replace("Bearer ", "");
        }
        return token;
    }

    protected boolean validateAccessToken(AccessJwt jwt) {
        if (jwt == null || jwt.getHeader() == null || jwt.getPayload() == null) {
            return false;
        }

        if (isTokenExpired(jwt)) {
            return false;
        }

        if (!isTokenAudValid(jwt)) {
            return false;
        }

        return true;
    }

    private boolean isTokenExpired(AccessJwt jwt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minimal = jwt.getPayload().getIssuedAt().minusSeconds(oauthTokenSkewSec);
        LocalDateTime maximal = jwt.getPayload().getExpiredAt().plusSeconds(oauthTokenSkewSec);

        return now.isBefore(minimal) || now.isAfter(maximal);
    }

    private boolean isTokenAudValid(AccessJwt jwt) {
        return activeServerAud.equals(jwt.getPayload().getAudience());
    }

}

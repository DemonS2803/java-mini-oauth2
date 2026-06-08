package ru.yandex.practicum.rs.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.web.HttpConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Order(100)
@Component
@RequiredArgsConstructor
public class OAuthRequestFilter extends OncePerRequestFilter {

    private final SecurityFilterUtil securityFilterUtil;

    @Value("${oauth.server.address}")
    private String oAuthServerAddress;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (securityFilterUtil.getAuthorizationToken(request) == null) {
            log.warn("Authenticate header is empty. Redirect to login page");
            redirectToOAuthLogin(response);
            return;
        }

        AccessJwt jwt = securityFilterUtil.getAccessTokenFromRequest(request);
        if (jwt == null || jwt.getPayload() == null) {
            log.error("Error during decoding JWT");
            response.sendError(400, "Failed to decode JWT");
            return;
        }

        if (!securityFilterUtil.validateAccessToken(jwt)) {
            log.info("Token is not active");
            redirectToOAuthLogin(response);
            return;
        }

        request.setAttribute(HttpConstants.JWT_ATTRIBUTE, jwt);
        log.info("Token is valid");
        filterChain.doFilter(request, response);

    }

    private void redirectToOAuthLogin(HttpServletResponse response) throws IOException {
        String redirectUrl = oAuthServerAddress + HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_LOGIN_PATH;
        log.info("Redirecting to {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }



}

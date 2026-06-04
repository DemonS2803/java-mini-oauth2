package ru.yandex.practicum.rs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.web.HttpConstants;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthRequestFilter extends OncePerRequestFilter {

    private final SecurityFilterUtil securityFilterUtil;

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


    }

    private void redirectToOAuthLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(HttpConstants.OAUTH_BASE_PATH + HttpConstants.OAUTH_TOKEN_PATH);
    }

}

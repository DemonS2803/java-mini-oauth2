package ru.yandex.practicum.rs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.yandex.practicum.common.oauth.permissions.SimpleRBAC;
import ru.yandex.practicum.common.oauth.util.AccessJwt;
import ru.yandex.practicum.common.web.HttpConstants;

import java.io.IOException;
import java.util.List;

@Slf4j
@Order(200)
@Component
public class ResourcePermissionsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("Filter JWT permissions");
        AccessJwt jwt = (AccessJwt) request.getAttribute(HttpConstants.JWT_ATTRIBUTE);
        if (jwt == null) {
            log.error("JWT is not in request attributes");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        List<String> scopes = jwt.getPayload().getScopes();
        if (!isRequestedResourceInScopes(request, scopes)) {
            log.error("JWT doesnt contain scope for requested resource");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        log.info("JWT has all permissions");
        filterChain.doFilter(request, response);
    }

    private boolean isRequestedResourceInScopes(HttpServletRequest request, List<String> scopes) {
        String path = request.getRequestURI();
        String needScope = mapMethodToScope(request.getMethod());
        for (String it : scopes) {
            String[] parts = it.split(":");
            String domain = parts[0];
            String scope = parts[1];
            String domainPath = HttpConstants.API_BASE_PATH + "/" + domain;
            // if requested resource is GET /api/payments, so JWT must contain scope payments:read.
            // read is GET and /api + /payments
            if (scope.equals(needScope) && path.startsWith(domainPath)) {
                return true;
            }
        }
        return false;
    }

    private String mapMethodToScope(String method) {
        return switch (method.toLowerCase()) {
            case "get" -> SimpleRBAC.READ_SCOPE;
            case "post" -> SimpleRBAC.EDIT_SCOPE;
            default -> null;
        };
    }

}

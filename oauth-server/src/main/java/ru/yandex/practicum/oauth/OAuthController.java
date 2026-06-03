package ru.yandex.practicum.oauth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;
import ru.yandex.practicum.common.oauth.dto.TokenInfoResponseDto;
import ru.yandex.practicum.common.web.HttpConstants;

@RestController
@RequestMapping(HttpConstants.OAUTH_BASE_PATH)
public class OAuthController {

    @PostMapping(HttpConstants.OAUTH_TOKEN_PATH)
    public AuthenticateResponseDto authenticate() {
        return null;
    }

    @PostMapping(HttpConstants.OAUTH_REFRESH_PATH)
    public AuthenticateResponseDto refresh() {
        return null;
    }

    @PostMapping(HttpConstants.OAUTH_REVOKE_PATH)
    public ResponseEntity<?> revoke() {
        return null;
    }

    @GetMapping(HttpConstants.OAUTH_INTROSPECT_PATH)
    public TokenInfoResponseDto introspect() {
        return null;
    }

}

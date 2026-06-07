package ru.yandex.practicum.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.common.web.HttpConstants;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(HttpConstants.OAUTH_BASE_PATH)
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping(HttpConstants.OAUTH_TOKEN_PATH)
    public AuthenticateResponseDto authenticate(@RequestBody AuthenticateRequestDto authRequest) {
        return oAuthService.authenticate(authRequest);
    }

    @PostMapping(HttpConstants.OAUTH_REFRESH_PATH)
    public AuthenticateResponseDto refresh(@RequestBody RefreshTokenRequestDto refreshRequest) {
        return oAuthService.refresh(refreshRequest);
    }

    @PostMapping(HttpConstants.OAUTH_REVOKE_PATH)
    public ResponseEntity<?> revoke(@RequestBody TokenFetchRequestDto revokeRequest) {
        oAuthService.revoke(revokeRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(HttpConstants.OAUTH_INTROSPECT_PATH)
    public TokenInfoResponseDto introspect(@RequestBody TokenFetchRequestDto tokenInfoRequest) {
        return oAuthService.tokenInfo(tokenInfoRequest);
    }

}

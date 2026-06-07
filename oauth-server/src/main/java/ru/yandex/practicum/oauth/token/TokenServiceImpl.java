package ru.yandex.practicum.oauth.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.common.exception.JwtDecodeException;
import ru.yandex.practicum.common.exception.JwtEncodeException;
import ru.yandex.practicum.common.exception.RefreshTokenInvalidException;
import ru.yandex.practicum.common.oauth.dto.*;
import ru.yandex.practicum.common.oauth.enums.TokenType;
import ru.yandex.practicum.common.oauth.util.*;
import ru.yandex.practicum.oauth.token.dto.AuthenticateClientCredentialsRequestDto;
import ru.yandex.practicum.oauth.token.dto.AuthenticatePasswordRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshIndexRepository refreshIndexRepository;
    private final RevocationRepository revocationRepository;

    @Value("${config.oauth.access.ttl.sec}")
    private Integer accessTtlSec;
    @Value("${config.oauth.refresh.ttl.days}")
    private Integer refreshTtlDays;
    @Value("${config.oauth.issuer}")
    private String issuer;
    @Value("${config.oauth.secret}")
    private String oauthApiSecret;

    @Override
    public AuthenticateResponseDto authenticate(AuthenticatePasswordRequestDto request) {
        log.info("Authenticating request with password flow: {}", request);
        RefreshToken refreshToken = TokenMapper.toRefreshToken(request);
        refreshToken.setExpiredAt(LocalDateTime.now().plusDays(refreshTtlDays));
        refreshToken = refreshIndexRepository.save(refreshToken);
        log.info("Saved refresh token entity: {}", refreshToken.getId());

        AccessJwt accessJwt = buildAccessJwt(request);
        String encodedAccessToken = JwtUtil.encode(accessJwt, oauthApiSecret);;
        log.info("Issued access token: {}", obfuscate(encodedAccessToken));

        RefreshJwt refreshJwt = buildRefreshJwt(request);
        refreshJwt.getPayload().setRefreshId(refreshToken.getId());
        String encodedRefreshToken = JwtUtil.encode(refreshJwt, oauthApiSecret);;
        log.info("Issued refresh token: {}", obfuscate(encodedRefreshToken));

        return buildResponse(encodedAccessToken, encodedRefreshToken);
    }

    @Override
    public AuthenticateResponseDto authenticate(AuthenticateClientCredentialsRequestDto request) {
        log.info("Authenticating request with client_credentials flow: {}", request);

        AccessJwt accessJwt = buildAccessJwt(request);
        String encodedAccessToken = JwtUtil.encode(accessJwt, request.getClientSecret());;
        log.info("Issued access token: {}", encodedAccessToken);

        // not pass refresh for client credentials flow
        return buildResponse(encodedAccessToken, null);
    }

    protected AuthenticateResponseDto buildResponse(String accessToken, String refreshToken) {
        if (accessToken == null) {
            log.error("Cannot build AuthenticateResponseDto with null access token");
            throw new IllegalArgumentException("Cannot build AuthenticateResponseDto with null access token");
        }
        return AuthenticateResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .issuer(issuer)
                .tokenAlg(JwtUtil.ALGORITHM)
                .accessTtlSec(accessTtlSec)
                .refreshTtlDays(refreshToken == null ? null : refreshTtlDays)
                .build();
    }

    private AccessJwt buildAccessJwt(AuthenticateRequest request) {
        if (request.getClientId() == null) {
            log.error("Cannot issue token with null client_id");
            throw new IllegalArgumentException("Cannot issue token with null client_id");
        }
        LocalDateTime now = LocalDateTime.now();
        AccessJwtPayload payload = AccessJwtPayload.builder()
                .issuer(issuer)
                .roles(request.getRoles())
                .scopes(request.getScopes())
                .clientId(request.getClientId())
                .sub(request.getClientId())
                .audience(request.getClientId())
                .expiredAt(now.plusSeconds(accessTtlSec))
                .tokenId(UUID.randomUUID())
                .issuedAt(now)
                .build();
        return AccessJwt.builder()
                .header(buildJwtHeader(TokenType.AT))
                .payload(payload)
                .build();
    }

    private RefreshJwt buildRefreshJwt(AuthenticateRequest request) {
        RefreshJwtPayload payload = RefreshJwtPayload.builder()
                .build();
        return RefreshJwt.builder()
                .header(buildJwtHeader(TokenType.RT))
                .payload(payload)
                .build();
    }

    private JwtHeader buildJwtHeader(TokenType tokenType) {
        return JwtHeader.builder()
            .type(tokenType)
            .algorithm(JwtUtil.ALGORITHM)
            .build();
    }


    @Override
    public AuthenticateResponseDto refresh(String token) {
        log.info("Refresh token {}", obfuscate(token));
        RefreshJwt jwt = JwtUtil.decodeRefreshAndVerify(token, oauthApiSecret);

        RefreshToken refreshToken = refreshIndexRepository.findRefreshTokenById(jwt.getPayload().getRefreshId()).get();

        if (!checkRefreshToken(refreshToken, jwt)) {
            throw new RefreshTokenInvalidException(
                    "Refresh token " + refreshToken.getId() + " is not active. Cannot issue new token");
        }

        AuthenticatePasswordRequestDto request = TokenMapper.toAuthRequest(refreshToken);
        request.setRoles(refreshToken.getUser().getRoles());
        log.info("Requesting new authenticate pair for request");
        refreshToken.setRotated(true);
        refreshIndexRepository.save(refreshToken);
        return authenticate(request);
    }

    @Override
    public void revokeAccessToken(String token) {
        log.info("Revoking access token: {}", obfuscate(token));
        AccessJwt jwt = JwtUtil.decodeAccessAndVerify(token, oauthApiSecret);
        Revocation revocation = new Revocation();
        revocation.setRevokedAt(LocalDateTime.now());
        revocation.setId(new TypeTokenId(jwt.getPayload().getTokenId(), TokenType.AT));
        revocationRepository.save(revocation);
        log.info("Saved new revocation entity for AT with id {}", jwt.getPayload().getTokenId());
    }

    @Override
    public void revokeRefreshToken(String token) {
        log.info("Revoking refresh token: {}", obfuscate(token));

        RefreshJwt jwt = JwtUtil.decodeRefreshAndVerify(token, oauthApiSecret);
        Revocation revocation = new Revocation();
        revocation.setRevokedAt(LocalDateTime.now());
        revocation.setId(new TypeTokenId(jwt.getPayload().getRefreshId(), TokenType.RT));
        revocationRepository.save(revocation);
        log.info("Saved new revocation entity for RT with id {}", jwt.getPayload().getRefreshId());
    }

    @Override
    public TokenInfoResponseDto getAccessTokenInfo(String token) {
        log.info("Fetch access token info: {}", obfuscate(token));
        AccessJwt jwt = JwtUtil.decode(token);
        TokenInfoResponseDto tokenInfo = TokenMapper.toTokenInfoResponse(jwt);

        if (LocalDateTime.now().isBefore(tokenInfo.getExpiredAt())) {
            log.debug("Token {} is still active", obfuscate(token));
            tokenInfo.setActive(true);
        }

        if (revocationRepository.existsById(new TypeTokenId(jwt.getPayload().getTokenId(), TokenType.AT))) {
            log.warn("User fetched info about revoked token {}", obfuscate(token));
            tokenInfo.setActive(false);
        }
        return tokenInfo;
    }

    @Override
    public TokenInfoResponseDto getRefreshTokenInfo(String token) {
        log.info("Fetch refresh token info: {}", obfuscate(token));
        RefreshJwt jwt = JwtUtil.decodeRefreshAndVerify(token, oauthApiSecret);
        RefreshToken refreshToken = refreshIndexRepository.getReferenceById(jwt.getPayload().getRefreshId());
        TokenInfoResponseDto tokenInfo = new TokenInfoResponseDto();
        tokenInfo.setClientId(refreshToken.getClient().getClientId());
        tokenInfo.setExpiredAt(refreshToken.getExpiredAt());
        tokenInfo.setActive(checkRefreshToken(refreshToken, jwt));

        return tokenInfo;
    }

    private String obfuscate(String token) {
        return token.substring(0, 15);
    }

    private boolean checkRefreshToken(RefreshToken refreshToken, RefreshJwt jwt) {
        if (LocalDateTime.now().isAfter(refreshToken.getExpiredAt())) {
            log.warn("Refresh token {} is not more active", refreshToken.getId());
            return false;
        }

        if (refreshToken.isRotated()) {
            log.warn("Refresh token {} was rotated", refreshToken.getId());
            return false;
        }

        if (revocationRepository.existsById(new TypeTokenId(jwt.getPayload().getRefreshId(), TokenType.RT))) {
            log.warn("User fetched info about revoked token {}", refreshToken.getId());
            return false;
        }
        return true;
    }

}

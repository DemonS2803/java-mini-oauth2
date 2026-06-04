package ru.yandex.practicum.oauth.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.common.exception.JwtDecodeException;
import ru.yandex.practicum.common.exception.JwtEncodeException;
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
        try {
            RefreshToken refreshToken = TokenMapper.toRefreshToken(request);
            refreshToken.setExpiredAt(LocalDateTime.now().plusDays(refreshTtlDays));
            refreshToken = refreshIndexRepository.save(refreshToken);
            log.info("Saved refresh token entity: {}", refreshToken);

            AccessJwt accessJwt = buildAccessJwt(request);
            String encodedAccessToken = JwtUtil.encode(accessJwt, oauthApiSecret);;
            log.info("Issued access token: {}", encodedAccessToken);

            RefreshJwt refreshJwt = buildRefreshJwt(request);
            refreshJwt.getPayload().setRefreshId(refreshToken.getId().toString());
            String encodedRefreshToken = JwtUtil.encode(refreshJwt, oauthApiSecret);;
            log.info("Issued refresh token: {}", encodedRefreshToken);

            return buildResponse(encodedAccessToken, encodedRefreshToken);
        } catch (Exception e) {
            log.error("Error while issue access token", e);
            throw new JwtEncodeException("Failed to issue token: " + e.getMessage());
        }
    }

    @Override
    public AuthenticateResponseDto authenticate(AuthenticateClientCredentialsRequestDto request) {
        log.info("Authenticating request with client_credentials flow: {}", request);
        try {
            AccessJwt accessJwt = buildAccessJwt(request);
            String encodedAccessToken = JwtUtil.encode(accessJwt, request.getClientSecret());;
            log.info("Issued access token: {}", encodedAccessToken);

            // not pass refresh for client credentials flow
            return buildResponse(encodedAccessToken, null);
        } catch (Exception e) {
            log.error("Error while issue access token", e);
            throw new JwtEncodeException("Failed to issue token: " + e.getMessage());
        }
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
        LocalDateTime now = LocalDateTime.now();
        RefreshJwtPayload payload = RefreshJwtPayload.builder()
//                .refreshId()
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
    public AuthenticateRequestDto refresh(AuthenticateRequestDto request) {
        return null;
    }

    @Override
    public void revoke(String token) {

    }

    @Override
    public TokenInfoResponseDto getAccessTokenInfo(String token) {
        log.info("Fetch access token info: {}", token.substring(0, 15));
        try {
            AccessJwt jwt = JwtUtil.decode(token);
            TokenInfoResponseDto tokenInfo = TokenMapper.toTokenInfoResponse(jwt);
            if (revocationRepository.existsById(new TypeTokenId(jwt.getPayload().getTokenId(), TokenType.AT))) {
                log.warn("User fetched info about revoked token {}", token.substring(0, 15));
                tokenInfo.setActive(false);
            }
            return tokenInfo;
        } catch (Exception e) {
            throw new JwtDecodeException("Failed to decode JWT: " + e.getMessage());
        }
    }

    @Override
    public TokenInfoResponseDto getRefreshTokenInfo(String token) {
        log.info("Fetch refresh token info: {}", token.substring(0, 15));
        try {
            RefreshJwt jwt = JwtUtil.decodeRefreshAndVerify(token, oauthApiSecret);
            RefreshToken refreshToken = refreshIndexRepository.getReferenceById(jwt.getPayload().getRefreshId());
            TokenInfoResponseDto tokenInfo = new TokenInfoResponseDto();
            tokenInfo.setClientId(refreshToken.getClient().getClientId());
            if (revocationRepository.existsById(new TypeTokenId(jwt.getPayload().getRefreshId(), TokenType.AT))) {
                log.warn("User fetched info about revoked token {}", token.substring(0, 15));
                tokenInfo.setActive(false);
            }
            return tokenInfo;
        } catch (Exception e){
            throw new JwtDecodeException("Failed to decode JWT: " + e.getMessage());
        }
    }

}

package ru.yandex.practicum.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.common.exception.*;
import ru.yandex.practicum.common.oauth.dto.ErrorResponseDto;

@Slf4j
@RestControllerAdvice
public class OAuthExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage());
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(ClientCredentialsInvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleClientCredentialsInvalidException(ClientCredentialsInvalidCredentialsException ex) {
        log.error("Invalid client credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UsernamePasswordInvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernamePasswordInvalidCredentialsExceptionException(UsernamePasswordInvalidCredentialsException ex) {
        log.error("Invalid password credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(NotPermittedException.class)
    public ResponseEntity<ErrorResponseDto> handleNotPermittedException(NotPermittedException ex) {
        log.error("Not permitted: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(JWTSignInvalidException.class)
    public ResponseEntity<ErrorResponseDto> handleJWTSignInvalidException(JWTSignInvalidException ex) {
        log.error("JWT sign invalid. token is imposter!: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(JwtDecodeException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtDecodeException(JwtDecodeException ex) {
        log.error("Failed to decode JWT: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorResponseDto> handleRefreshTokenInvalidException(RefreshTokenInvalidException ex) {
        log.error("Refresh token invalid: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UnknownAuthenticationGrantTypeException.class)
    public ResponseEntity<ErrorResponseDto> handleUnknownAuthenticationGrantTypeException(UnknownAuthenticationGrantTypeException ex) {
        log.error("Unknown auth grant type: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(UnknownTokenTypeHintException.class)
    public ResponseEntity<ErrorResponseDto> handleUnknownTokenTypeHintException(UnknownTokenTypeHintException ex) {
        log.error("Unknown token type hint: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponseDto(ex.getMessage()));
    }

}

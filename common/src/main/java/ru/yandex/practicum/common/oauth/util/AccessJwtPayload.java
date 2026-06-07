package ru.yandex.practicum.common.oauth.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessJwtPayload extends JwtPayload {

    @JsonProperty(value = "jti")
    private UUID tokenId;
    @JsonProperty(value = "iss")
    private String issuer;
    @JsonProperty(value = "aud")
    private String audience;
    @JsonProperty(value = "sub")
    private String sub;
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "scopes")
    private List<String> scopes;
    @JsonProperty(value = "roles")
    private List<String> roles;
    @JsonProperty(value = "iat")
    private LocalDateTime issuedAt;
    @JsonProperty(value = "exp")
    private LocalDateTime expiredAt;

}

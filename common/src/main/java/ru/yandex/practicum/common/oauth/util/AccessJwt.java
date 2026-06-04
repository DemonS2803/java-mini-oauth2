package ru.yandex.practicum.common.oauth.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessJwt extends BaseJwt {

    private JwtHeader header;
    private AccessJwtPayload payload;

}

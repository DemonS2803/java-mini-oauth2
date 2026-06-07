package ru.yandex.practicum.common.oauth.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessJwt extends BaseJwt {

    private JwtHeader header;
    private AccessJwtPayload payload;

}

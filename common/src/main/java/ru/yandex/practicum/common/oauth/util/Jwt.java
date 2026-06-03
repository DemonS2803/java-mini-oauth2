package ru.yandex.practicum.common.oauth.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jwt {

    private JwtHeader header;
    private JwtPayload payload;

}

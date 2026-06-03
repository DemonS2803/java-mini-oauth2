package ru.yandex.practicum.common.oauth.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.common.oauth.enums.TokenType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtHeader {

    @JsonProperty(value = "typ")
    private TokenType type;
    @JsonProperty(value = "alg")
    private String algorithm;

}

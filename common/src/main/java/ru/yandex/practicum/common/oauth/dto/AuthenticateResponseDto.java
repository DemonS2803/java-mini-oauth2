package ru.yandex.practicum.common.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthenticateResponseDto {

    private String accessToken;
    private String refreshToken;
    private String issuer;
    private String aud;
    private Integer accessTtlSec;
    private Integer refreshTtlDays;
    private String tokenAlg;

}

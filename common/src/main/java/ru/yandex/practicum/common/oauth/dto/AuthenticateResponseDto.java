package ru.yandex.practicum.common.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticateResponseDto {

    private String accessToken;
    private String refreshToken;
    private String issuer;
    private String aud;
    private Integer accessTtlSec;
    private Integer refreshTtlDays;
    private String tokenAlg;

}

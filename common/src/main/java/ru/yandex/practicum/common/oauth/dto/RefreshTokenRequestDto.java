package ru.yandex.practicum.common.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RefreshTokenRequestDto {

    private String grantType;
    private String refreshToken;
    private String clientId;
    private String clientSecret;

    @Override
    public String toString() {
        return "RefreshTokenRequestDto{" +
                "grantType='" + grantType + '\'' +
                ", refreshToken= *** " +
                ", clientId='" + clientId + '\'' +
                ", clientSecret= *** " +
                '}';
    }
}

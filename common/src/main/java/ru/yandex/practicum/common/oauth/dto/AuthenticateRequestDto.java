package ru.yandex.practicum.common.oauth.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthenticateRequestDto {

    private String grantType;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private List<String> scopes;

    @Override
    public String toString() {
        return "AuthenticateRequestDto{" +
                "grantType='" + grantType + '\'' +
                ", username='" + username + '\'' +
                ", password= *** " +
                ", clientId='" + clientId + '\'' +
                ", clientSecret= *** " +
                ", scopes=" + scopes +
                '}';
    }
}

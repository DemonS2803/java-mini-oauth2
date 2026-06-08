package ru.yandex.practicum.common.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenFetchRequestDto {

    private String token;
    private String tokenTypeHint;

    @Override
    public String toString() {
        return "TokenFetchRequestDto{" +
                "token= ***" +
                ", tokenTypeHint='" + tokenTypeHint + '\'' +
                '}';
    }
}

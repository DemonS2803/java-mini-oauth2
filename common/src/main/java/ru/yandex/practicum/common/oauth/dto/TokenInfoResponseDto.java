package ru.yandex.practicum.common.oauth.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenInfoResponseDto {

    private boolean active;
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "exp")
    private LocalDateTime expiredAt;


}

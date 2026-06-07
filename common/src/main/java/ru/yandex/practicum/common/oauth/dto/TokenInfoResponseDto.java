package ru.yandex.practicum.common.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenInfoResponseDto {

    private boolean active;
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "exp")
    private LocalDateTime expiredAt;


}

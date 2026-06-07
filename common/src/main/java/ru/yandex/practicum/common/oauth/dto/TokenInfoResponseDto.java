package ru.yandex.practicum.common.oauth.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenInfoResponseDto {

    private boolean active;
    @JsonProperty(value = "client_id")
    private String clientId;
    @JsonProperty(value = "exp")
    private LocalDateTime expiredAt;


}

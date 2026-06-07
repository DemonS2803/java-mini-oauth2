package ru.yandex.practicum.common.oauth.util;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshJwtPayload extends JwtPayload {

    @JsonProperty(value = "refresh_id")
    private UUID refreshId;

}

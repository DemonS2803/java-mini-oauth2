package ru.yandex.practicum.oauth.token;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import ru.yandex.practicum.common.oauth.enums.TokenType;

import java.util.UUID;

@Embeddable
@AllArgsConstructor
public class TypeTokenId {

    private UUID tokenId;
    private TokenType tokenType;

}

package ru.yandex.practicum.oauth.token;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import ru.yandex.practicum.common.oauth.enums.TokenType;

@Embeddable
@AllArgsConstructor
public class TypeTokenId {

    private Long tokenId;
    private TokenType tokenType;

}

package ru.yandex.practicum.oauth.token;

import java.util.UUID;

import jakarta.persistence.Embeddable;

import ru.yandex.practicum.common.oauth.enums.TokenType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class TypeTokenId {

    private UUID tokenId;
    private TokenType tokenType;

}

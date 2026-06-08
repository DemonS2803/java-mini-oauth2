package ru.yandex.practicum.oauth.token;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import ru.yandex.practicum.common.oauth.enums.TokenType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class TypeTokenId {

    @Column(name = "token_id")
    private UUID tokenId;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

}

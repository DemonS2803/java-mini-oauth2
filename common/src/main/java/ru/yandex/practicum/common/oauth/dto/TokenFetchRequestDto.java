package ru.yandex.practicum.common.oauth.dto;

import lombok.Data;

@Data
public class TokenFetchRequestDto {

    private String token;
    private String tokenTypeHint;

}

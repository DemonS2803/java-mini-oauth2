package ru.yandex.practicum.oauth.token;

import ru.yandex.practicum.common.oauth.dto.AuthenticateRequestDto;
import ru.yandex.practicum.common.oauth.dto.AuthenticateResponseDto;

public interface TokenService {

    AuthenticateResponseDto authenticate(AuthenticateRequestDto request);

}

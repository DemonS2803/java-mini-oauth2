package ru.yandex.practicum.common.oauth.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
class JwtUtil {

    public static final String ALGORITHM = "HmacSHA256";

    public static ObjectMapper mapper = buildMapper();

    // 1) Кодирование данных в токен (header.payload.signature)
    public static String encode(Jwt token, String secret) throws Exception {
        String encodedToken = encodeToken(token);
        String signature = signToken(encodedToken, secret);

        return String.format("%s.%s", encodedToken, signature);
    }

    private static String encodeToken(Jwt token) throws JsonProcessingException {
        return String.format("%s.%s", encodeHeader(token), encodePayload(token));
    }

    private static String encodeHeader(Jwt token) throws JsonProcessingException {
        String json = mapper.writeValueAsString(token.getHeader());
        return base64UrlEncode(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String encodePayload(Jwt token) throws JsonProcessingException {
        String json = mapper.writeValueAsString(token.getPayload());
        return base64UrlEncode(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Simply decode JWT to object without any checks
     * @param token - string with at least 2 dot-separated parts
     * @return
     * @throws Exception
     */
    public static Jwt decode(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;

        String header = parts[0];
        String payload = parts[1];

        return decodeToken(header, payload);
    }

    // 2) Проверка токена и возврат данных, если всё ок, иначе null
    public static Jwt decodeAndVerify(String token, String secret) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) return null;

        String header = parts[0];
        String payload = parts[1];
        String signature = parts[2];

        String toSign = header + "." + payload;
        String expectedSig = signToken(toSign, secret);
        if (!expectedSig.equals(signature)) {
            return null; // подпись не совпала
        }

        return decodeToken(header, payload);
    }

    private static Jwt decodeToken(String header, String payload) throws JsonProcessingException {
        Jwt token = new Jwt();
        String decodedHeader = new String(base64UrlDecode(header));
        String decodedPayload = new String(base64UrlDecode(payload));
//        log.info("decoded header: {}", decodedHeader);
//        log.info("decoded payload: {}", decodedPayload);
        token.setHeader(mapper.readValue(decodedHeader, JwtHeader.class));
        token.setPayload(mapper.readValue(decodedPayload, JwtPayload.class));
        return token;
    }

    // Подпись HS256
    private static String signToken(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec key =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(key);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(rawHmac);
    }

    // Base64URL encode (без =, +, /)
    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String s) {
        return Base64.getUrlDecoder().decode(s);
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        return mapper;
    }

}

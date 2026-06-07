package ru.yandex.practicum.common.oauth.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import ru.yandex.practicum.common.exception.JWTSignInvalidException;
import ru.yandex.practicum.common.exception.JwtDecodeException;
import ru.yandex.practicum.common.exception.JwtEncodeException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {

    public static final String ALGORITHM = "HmacSHA256";

    public static ObjectMapper mapper = buildMapper();

    /**
     * header.payload.signature
     */
    public static String encode(BaseJwt token, String secret) {
        try {
            String encodedToken = encodeToken(token);
            String signature = signToken(encodedToken, secret);

            return String.format("%s.%s", encodedToken, signature);
        } catch (Exception e) {
            throw new JwtEncodeException("Failed to encode JWT");
        }
    }

    private static String encodeToken(BaseJwt token) throws JsonProcessingException {
        return String.format("%s.%s", encodeHeader(token.getHeader()), encodePayload(token.getPayload()));
    }

    private static String encodeHeader(JwtHeader header) throws JsonProcessingException {
        String json = mapper.writeValueAsString(header);
        return base64UrlEncode(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String encodePayload(JwtPayload payload) throws JsonProcessingException {
        String json = mapper.writeValueAsString(payload);
        return base64UrlEncode(json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Simply decode JWT to object without any checks
     * @param token - string with at least 2 dot-separated parts
     * @return
     * @throws Exception
     */
    public static AccessJwt decode(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }

            String header = parts[0];
            String payload = parts[1];

            return decodeToken(header, payload);
        } catch (Exception e) {
            log.error("Error while decoding refresh JWT: {}", e.getMessage());
            throw new JwtDecodeException("Failed to decode refresh JWT");
        }
    }

    public static AccessJwt decodeAccessAndVerify(String token, String secret) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String toSign = header + "." + payload;
            String expectedSig = signToken(toSign, secret);
            if (!expectedSig.equals(signature)) {
                return null;
            }

            AccessJwt accessJwt = new AccessJwt();
            accessJwt.setHeader(decodeHeader(header));
            accessJwt.setPayload(decodeAccessPayload(payload));
            return accessJwt;
        } catch (Exception e) {
            log.error("Error while decoding refresh JWT: {}", e.getMessage());
            throw new JwtDecodeException("Failed to decode refresh JWT");
        }
    }

    public static RefreshJwt decodeRefreshAndVerify(String token, String secret) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String toSign = header + "." + payload;
            String expectedSig = signToken(toSign, secret);
            if (!expectedSig.equals(signature)) {
                throw new JWTSignInvalidException();
            }

            RefreshJwt refreshJwt = new RefreshJwt();
            refreshJwt.setHeader(decodeHeader(header));
            refreshJwt.setPayload(decodeRefreshPayload(payload));
            return refreshJwt;
        } catch (JWTSignInvalidException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while decoding refresh JWT: {}", e.getMessage());
            throw new JwtDecodeException("Failed to decode refresh JWT");
        }
    }

    private static AccessJwt decodeToken(String header, String payload) throws JsonProcessingException {
        AccessJwt token = new AccessJwt();
        String decodedHeader = new String(base64UrlDecode(header));
        String decodedPayload = new String(base64UrlDecode(payload));
        token.setHeader(mapper.readValue(decodedHeader, JwtHeader.class));
        token.setPayload(mapper.readValue(decodedPayload, AccessJwtPayload.class));
        return token;
    }

    private static JwtHeader decodeHeader(String header) throws JsonProcessingException {
        String decodedHeader = new String(base64UrlDecode(header));
        return mapper.readValue(decodedHeader, JwtHeader.class);
    }

    private static AccessJwtPayload decodeAccessPayload(String payload) throws JsonProcessingException {
        String decodedPayload = new String(base64UrlDecode(payload));
        return mapper.readValue(decodedPayload, AccessJwtPayload.class);
    }

    private static RefreshJwtPayload decodeRefreshPayload(String payload) throws JsonProcessingException {
        String decodedPayload = new String(base64UrlDecode(payload));
        return mapper.readValue(decodedPayload, RefreshJwtPayload.class);
    }

    private static String signToken(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec key =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(key);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(rawHmac);
    }

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
        return mapper;
    }

}

package ru.yandex.practicum.common.web;

public class HttpConstants {

    // OAuth paths
    public static final String OAUTH_BASE_PATH = "/oauth";
    public static final String OAUTH_TOKEN_PATH = "/token";
    public static final String OAUTH_REFRESH_PATH = "/token/refresh";
    public static final String OAUTH_REVOKE_PATH = "/revoke";
    public static final String OAUTH_INTROSPECT_PATH = "/introspect";
    // Emulate login page
    public static final String OAUTH_LOGIN_PATH = "/login";

    // Public API paths
    public static final String API_BASE_PATH = "/api";
    public static final String PAYMENTS_PATH = API_BASE_PATH + "/payments";

    // Request attributes
    public static final String JWT_ATTRIBUTE = "jwt";

}

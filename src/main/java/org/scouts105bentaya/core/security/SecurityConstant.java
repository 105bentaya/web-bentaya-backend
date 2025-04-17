package org.scouts105bentaya.core.security;

final class SecurityConstant {
    static final String AUTH_LOGIN_URL = "/api/login";

    static final String[] AUTH_WHITELIST = {
        "/api/inscription/form",
        "/api/blog/public/**",
        "/api/complaint/form",
        "/api/contact-message/form",
        "/api/partnership/form",
        "/api/pre-scout/form",
        "/api/pre-scouter/form",
        "/api/settings/get/**",
        "/api/tpv/notification/**",
        "/api/user/password/**",
        "/api/donation/public/**",
        "/api/booking/public/**",
        "/api/senior/form",
        "/api/event/public/**",
        "/api/scout-center/public/**",
        "/api/jamboree/public/**",
    };

    static final String API_URL = "/api/**";

    // JWT token defaults
    static final String TOKEN_HEADER = "Authorization";
    static final String TOKEN_PREFIX = "Bearer ";
    static final String TOKEN_TYPE = "JWT";

    private SecurityConstant() {
        throw new IllegalStateException("Cannot create instance of static util class");
    }
}

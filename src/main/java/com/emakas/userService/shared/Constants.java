package com.emakas.userService.shared;

public class Constants {
    /**
     * @deprecated Use Constants.Chars.SEPERATOR instead
     */
    @Deprecated
    public static final String SEPARATOR = ":";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String BEARER_TOKEN = "Bearer";
    public static final String AUTH_COOKIE_PARAMETER = "AUTH-TOKEN";
    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_PROFILE = "profile";
    public static final String SCOPE_EMAIL = "email";
    /**
     * @deprecated Use Constants.Chars.ONE_SPACE instead
     */
    @Deprecated
    public static final String ONE_SPACE = " ";

    public static class Chars {
        public static final String ONE_SPACE = " ";
        public static final String SEPARATOR = ":";
    }

    public static final class JwtClaims {
        public static final class AccessTokenClaims {
            public static final String SCOPE = "scope";
            public static final String PRINCIPAL_TYPE = "principal_type";
            public static final String CLIENT_ID = "client_id";
        }
        public static final class RefreshTokenClaims {
            public static final String USER_LOGIN = "user_login";
        }
    }
}

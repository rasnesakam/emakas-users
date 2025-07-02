package com.emakas.userService.shared.enums;


public enum GrantType {
    AUTHORIZATION_CODE,
    CLIENT_CREDENTIALS,
    REFRESH_TOKEN,
    UNDEFINED;

    public static GrantType getGrantType(String grantType) {
        if (grantType == null) return UNDEFINED;
        try {
            return GrantType.valueOf(grantType.toUpperCase());
        }catch (IllegalArgumentException e) {
            return UNDEFINED;
        }
    }
}

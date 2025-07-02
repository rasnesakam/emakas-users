package com.emakas.userService.shared.enums;

import java.util.Optional;

public enum GrantType {
    AUTHORIZATION_CODE,
    CLIENT_CREDENTIALS,
    REFRESH_TOKEN;

    public static Optional<GrantType> getGrantType(String grantType) {
        try {
            return Optional.of(GrantType.valueOf(grantType.toUpperCase()));
        }catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

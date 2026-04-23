package com.emakas.userService.shared.enums;


import com.emakas.userService.cmdRunner.InitializeRootVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public enum GrantType {
    AUTHORIZATION_CODE,
    CLIENT_CREDENTIALS,
    REFRESH_TOKEN,
    UNDEFINED;

    public static GrantType getGrantType(String grantType) {
        if (grantType == null) return UNDEFINED;
        Logger logger = LoggerFactory.getLogger(GrantType.class);
        logger.info("Grant type is {}", grantType);
        try {
            return GrantType.valueOf(grantType.toUpperCase(Locale.ENGLISH));
        }catch (IllegalArgumentException e) {
            return UNDEFINED;
        }
    }
}

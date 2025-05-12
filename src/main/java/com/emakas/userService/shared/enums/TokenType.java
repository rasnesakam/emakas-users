package com.emakas.userService.shared.enums;

import org.springframework.lang.NonNull;

public enum TokenType {
    APP,
    USR,
    UNDEFINED;

    private static final String SEPERATOR = ":";
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    public static TokenType fromString(@NonNull String tokenSubject) {
        if (tokenSubject.startsWith(APP.toString().concat(SEPERATOR)))
            return APP;
        else if (tokenSubject.startsWith(USR.toString().concat(SEPERATOR)))
            return USR;
        else
            return UNDEFINED;
    }
}

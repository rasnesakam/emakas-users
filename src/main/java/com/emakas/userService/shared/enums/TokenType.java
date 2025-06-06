package com.emakas.userService.shared.enums;

import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.emakas.userService.shared.Constants.SEPARATOR;

public enum TokenType {
    APP,
    USR,
    UNDEFINED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    public static TokenType fromString(@NonNull String tokenSubject) {
        if (tokenSubject.startsWith(APP.toString().concat(SEPARATOR)))
            return APP;
        else if (tokenSubject.startsWith(USR.toString().concat(SEPARATOR)))
            return USR;
        else
            return UNDEFINED;
    }

}

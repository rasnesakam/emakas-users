package com.emakas.userService.shared.enums;

import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenType {
    APP,
    USR,
    UNDEFINED;

    private static final String SEPARATOR = ":";
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
    public static Optional<String> getCleanSubject(@NonNull TokenType tokenType, String tokenSubject) {
        Pattern tokenPattern = Pattern.compile(String.format("^%s%s(.*)$",tokenType, SEPARATOR));
        Matcher matcher = tokenPattern.matcher(tokenSubject);
        if (matcher.matches()) {
            return Optional.of(matcher.group(1));
        }
        return Optional.empty();
    }
}

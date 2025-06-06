package com.emakas.userService.shared.enums;

import java.util.Locale;

public enum AccessModifier {
    READ,
    WRITE,
    READ_WRITE;


    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}

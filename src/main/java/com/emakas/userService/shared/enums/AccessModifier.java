package com.emakas.userService.shared.enums;

import java.util.Locale;

public enum AccessModifier {
    READ,
    WRITE,
    READ_WRITE;

    public boolean doesCover(AccessModifier oth) {
        return this == READ_WRITE
                || (this == READ && oth == READ)
                || (this == WRITE && oth == WRITE);
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}

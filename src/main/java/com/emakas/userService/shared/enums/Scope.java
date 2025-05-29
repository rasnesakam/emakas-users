package com.emakas.userService.shared.enums;

import java.util.stream.Stream;

@Deprecated
public enum Scope   {
    USERS_R(AccessModifier.READ),
    USERS_W(AccessModifier.WRITE),
    USERS_RW(AccessModifier.READ, AccessModifier.WRITE),
    ;

    private final AccessModifier[] modifiers;

    Scope(AccessModifier... modifier) {
        this.modifiers = modifier;
    }

    @Override
    public String toString() {
        return String.format(
                "%s/%s",
                super.toString().toLowerCase(),
                Stream.of(this.modifiers)
                        .map(Enum::toString).reduce(String::concat)
        );
    }
}

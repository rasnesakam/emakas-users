package com.emakas.userService.shared.enums;

public enum PrincipalType {
    APP,
    USER,
    UNDEFINED;

    @Override
    public String toString() {
        return name().toUpperCase();
    }

}

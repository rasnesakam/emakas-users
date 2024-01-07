package com.emakas.userService.shared.enums;

public enum Scope   {
    READ("read"),
    WRITE("write");

    private final String scope;

    Scope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }
}

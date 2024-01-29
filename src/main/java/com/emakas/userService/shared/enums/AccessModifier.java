package com.emakas.userService.shared.enums;

public enum AccessModifier {
    READ,
    WRITE;


    @Override
    public String toString() {
        return super.toString().substring(0,1).toLowerCase();
    }
}

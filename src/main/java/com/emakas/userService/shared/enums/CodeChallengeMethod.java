package com.emakas.userService.shared.enums;

public enum CodeChallengeMethod {
    SHA_256("S256"),
    PLAIN("plain"),
    UNKNOWN("unknown");

    private String normalizedName;
    CodeChallengeMethod(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }
    @Override
    public String toString() {
        return this.normalizedName;
    }
}

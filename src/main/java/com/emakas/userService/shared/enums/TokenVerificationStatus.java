package com.emakas.userService.shared.enums;

import lombok.Getter;

@Getter
public enum TokenVerificationStatus {
    SUCCESS,
    EXPIRED,
    INVALID_SIGNATURE,
    INVALID_ALGORITHM,
    FAILED;

    private Exception exception;

    public TokenVerificationStatus withException(Exception exception){
        this.exception = exception;
        return this;
    }
}

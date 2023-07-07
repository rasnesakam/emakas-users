package com.emakas.userService.shared;

public enum TokenVerificationStatus {
    SUCCESS,
    EXPIRED,
    INVALID_SIGNATURE,
    INVALID_ALGORITHM,
    FAILED;

    private Exception exception;
    public Exception getException(){
        return this.exception;
    }
    public TokenVerificationStatus withException(Exception e){
        this.exception = exception;
        return this;
    }
}

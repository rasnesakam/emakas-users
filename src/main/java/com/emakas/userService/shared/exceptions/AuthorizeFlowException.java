package com.emakas.userService.shared.exceptions;

public class AuthorizeFlowException extends Exception {

    private String title;
    private String description;


    public AuthorizeFlowException(String title, String description) {
        super(String.format("%s: %s", title, description));
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    
}

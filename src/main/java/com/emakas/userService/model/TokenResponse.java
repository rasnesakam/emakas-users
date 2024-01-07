package com.emakas.userService.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String userName;
    private String name;
    private String surname;
    private String email;
    private String token;

    public TokenResponse(String userName, String name, String surname, String email, String token) {
        this.userName = userName;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.token = token;
    }

    public TokenResponse() {
    }
}

package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String token;

    public TokenResponseDto(String username, String name, String surname, String email, String token) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.token = token;
    }

    public TokenResponseDto() {
    }
}

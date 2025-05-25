package com.emakas.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenResponseDto {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String accessToken;
    private String refreshToken;

}

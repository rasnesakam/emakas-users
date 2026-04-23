package com.emakas.userService.domain.auth;

import com.emakas.userService.shared.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal {
    UUID userId;
    String username;
    String email;
    Set<String> authorities;
    TokenType tokenType;
}

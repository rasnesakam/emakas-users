package com.emakas.userService.dto;

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
public class LoginSessionDto {
    private UUID clientId;
    private UUID sessionId;
    private String redirectUri;
    private Set<String> requestedScopes;
    private String state;
}

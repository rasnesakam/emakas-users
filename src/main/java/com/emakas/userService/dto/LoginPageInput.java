package com.emakas.userService.dto;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginPageInput {
    private String responseType;
    private UUID clientId;
    private String redirectUri;
    private String scope;
    private String state;
    private String codeChallenge;
    private String codeChallengeMethodString;
    private HttpServletRequest request;
}

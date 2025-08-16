package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("client_id")
    private UUID clientId;

    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("redirect_uri")
    private String redirectUri;

    @JsonProperty("requested_scopes")
    private Set<String> requestedScopes;

    @JsonProperty("code_challenge")
    private String codeChallenge;

    private String state;

    private String audience;
}

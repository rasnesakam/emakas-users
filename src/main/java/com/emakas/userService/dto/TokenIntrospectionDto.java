package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntrospectionDto {
    private boolean active;
    private String sub;
    private long exp;
    private long iat;
    private String scope;
    @JsonProperty("client_id")
    private String clientId;
    private String username;
    @JsonProperty("token_type")
    private String tokenType;
    private String aud;
    private String iss;
}

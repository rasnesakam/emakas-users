package com.emakas.userService.dto;

import com.emakas.userService.shared.authGroups.AuthorizationCodeGrant;
import com.emakas.userService.shared.authGroups.RefreshTokenGrant;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequestDto {

    @NotBlank
    @JsonProperty("grant_type")
    private String grantType;

    @NotBlank(groups = AuthorizationCodeGrant.class)
    @JsonProperty("code")
    private String code;

    @JsonProperty("client_id")
    private String clientId;

    @NotBlank
    @JsonProperty("client_secret")
    private String clientSecret;

    @NotBlank(groups = AuthorizationCodeGrant.class)
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @NotBlank(groups = RefreshTokenGrant.class)
    @JsonProperty("refresh_token")
    private String refreshToken;
}

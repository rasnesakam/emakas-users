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
public class OpenIdConfigDto {
    private String issuer;
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    @JsonProperty("jwks_uri")
    private String jwksUri;
    @JsonProperty("scopes_supported")
    private String[] scopesSupported;
    @JsonProperty("response_types_supported")
    private String[] responseTypesSupported;
    @JsonProperty("token_endpoint_auth_methods_supported")
    private String[] tokenEndpointAuthMethodsSupported;
    @JsonProperty("id_token_signing_alg_values_supported")
    private String[] idTokenSigningAlgValuesSupported;
    @JsonProperty("grant_types_supported")
    private String[] grantTypesSupported;
    @JsonProperty("subject_types_supported")
    private String[] subjectTypesSupported;
}

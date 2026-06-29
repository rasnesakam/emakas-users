package com.emakas.userService.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oidc")
public class OidcConfig {
    private String baseUrl;
    private List<String> supportedScopes;
    private List<String> responseTypesSupported;
    private List<String> tokenEndpointAuthMethodsSupported;
    private List<String> idTokenSigningAlgValuesSupported;
    private List<String> subjectTypesSupported;
}
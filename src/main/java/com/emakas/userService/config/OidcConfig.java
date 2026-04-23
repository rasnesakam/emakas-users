package com.emakas.userService.config;

import com.emakas.userService.shared.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Getter
@Setter
@Component
public class OidcConfig {
    private final Set<String> supportedScopes = Set.of(
            "openid",
            "profile",
            "email"
    );

    private final Set<String> responseTypesSupported = Set.of(
            "code",
            "client_credentials"
    );

    public Set<String> tokenEndpointAuthMethodsSupported = Set.of(
            "Bearer"
    );

    public boolean isScopeSupported(String scope) {
        if (scope.contains(Constants.ONE_SPACE))
            return Arrays.stream(scope.split(Constants.ONE_SPACE)).allMatch(supportedScopes::contains);
        return supportedScopes.contains(scope);
    }

    public boolean isResponseTypeSupported(String responseType) {
        return responseTypesSupported.contains(responseType);
    }
}

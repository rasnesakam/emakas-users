package com.emakas.userService.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "emakas IAM", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "http://localhost:8080/page/auth/login",
                        tokenUrl = "http://localhost:8080/api/oauth/token"
                        //scopes = {
                        //        @io.swagger.v3.oas.annotations.security.OAuthScope(name = "read", description = "Read access"),
                        //        @io.swagger.v3.oas.annotations.security.OAuthScope(name = "write", description = "Write access")
                        //}
                )
        )
)
public class OpenApiConfig {
}

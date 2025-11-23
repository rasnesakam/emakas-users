package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.TokenIntrospectionDto;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.Token;
import com.emakas.userService.model.User;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.GrantType;
import com.emakas.userService.shared.enums.TokenTargetType;
import com.emakas.userService.shared.enums.TokenVerificationStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/oauth")
public class OAuthController {

    private final TokenPermissionEvaluator tokenPermissionEvaluator;
    private final OauthFlowManager oauthFlowManager;
    private final TokenService tokenService;

    @Autowired
    public OAuthController(TokenPermissionEvaluator tokenPermissionEvaluator, OauthFlowManager oauthFlowManager, TokenService tokenService) {
        this.tokenPermissionEvaluator = tokenPermissionEvaluator;
        this.oauthFlowManager = oauthFlowManager;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "introspection", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenIntrospectionDto> introspection(@RequestParam("token") String token, @RequestParam(name = "token_type_hint", required = false) String tokenHint) {
        return tokenService.introspect(token).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    //TODO: Use PreAuthorize annotation
    @GetMapping("token/verify")
    public ResponseEntity<?> authorizeToken(@RequestParam(required = false) String targetResource, @RequestParam(required = false) String requestedPermission){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication){
            boolean parametersAreNotNullOrEmpty = targetResource != null && !targetResource.isBlank()
                    && requestedPermission != null && !requestedPermission.isBlank();
            if (parametersAreNotNullOrEmpty){
                if (tokenPermissionEvaluator.hasPermission(jwtAuthentication, targetResource, requestedPermission))
                    return ResponseEntity.ok().build();
                else
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Failed");
            }
            if (jwtAuthentication.isAuthenticated())
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( "Authorization failed");

        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication must made by Jwt toekens");
    }


    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<TokenResponseDto> getToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "client_id", required = false) UUID clientId,
            @RequestParam(name = "client_secret", required = false) String clientSecret,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            @RequestParam(name = "refresh_token", required = false) String refreshToken,
            @RequestParam(name = "code_verifier", required = false) String codeVerifier,
            @RequestParam(name = "scope", required = false) String[] scopes,
            HttpServletRequest request){
        return switch (GrantType.getGrantType(grantType)){
            case AUTHORIZATION_CODE -> oauthFlowManager.handleAuthorizationFlow(code, clientId, clientSecret, codeVerifier, redirectUri);
            case CLIENT_CREDENTIALS -> oauthFlowManager.handleClientCredentialsFlow(clientId, clientSecret, scopes);
            case REFRESH_TOKEN -> oauthFlowManager.handleRefreshTokenFlow(refreshToken, request);
            default -> ResponseEntity.badRequest().build();
        };
    }
}

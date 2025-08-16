package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.GrantType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/oauth")
public class OAuthController {

    private final TokenManager tokenManager;
    private final TokenService tokenService;
    private final UserLoginService userLoginService;
    private final TokenPermissionEvaluator tokenPermissionEvaluator;
    private final ResourcePermissionService resourcePermissionService;
    private final OauthFlowManager oauthFlowManager;

    @Autowired
    public OAuthController(TokenService tokenService, TokenManager tokenManager, UserLoginService userLoginService, TokenPermissionEvaluator tokenPermissionEvaluator, ResourcePermissionService resourcePermissionService, OauthFlowManager oauthFlowManager){
        this.tokenService = tokenService;
        this.tokenManager = tokenManager;
        this.userLoginService = userLoginService;
        this.tokenPermissionEvaluator = tokenPermissionEvaluator;
        this.resourcePermissionService = resourcePermissionService;
        this.oauthFlowManager = oauthFlowManager;
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

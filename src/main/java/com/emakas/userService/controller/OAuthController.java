package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenRequestDto;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import com.emakas.userService.service.*;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.GrantType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

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
    public ResponseEntity<Response<TokenResponseDto>> getToken(@Valid @ModelAttribute TokenRequestDto tokenRequestDto, HttpServletRequest request){
        return switch (GrantType.getGrantType(tokenRequestDto.getGrantType())){
            case AUTHORIZATION_CODE -> oauthFlowManager.handleAccessTokenFlow(tokenRequestDto.getCode(), tokenRequestDto.getClientId(), tokenRequestDto.getClientSecret());
            case CLIENT_CREDENTIALS -> oauthFlowManager.handleClientCredentialsFlow(tokenRequestDto.getClientId(), tokenRequestDto.getClientSecret());
            case REFRESH_TOKEN -> oauthFlowManager.handleRefreshTokenFlow(tokenRequestDto.getRefreshToken(), request);
            default -> ResponseEntity.badRequest().body(Response.of("Invalid Grant Type"));
        };
    }
}

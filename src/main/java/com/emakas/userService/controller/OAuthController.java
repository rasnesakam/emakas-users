package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import com.emakas.userService.service.ResourcePermissionService;
import com.emakas.userService.service.UserLoginService;
import com.emakas.userService.shared.Constants;
import com.emakas.userService.shared.TokenManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.emakas.userService.service.UserService;
import com.emakas.userService.service.TokenService;

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

    @Autowired
    public OAuthController(TokenService tokenService, TokenManager tokenManager, UserLoginService userLoginService, TokenPermissionEvaluator tokenPermissionEvaluator, ResourcePermissionService resourcePermissionService){
        this.tokenService = tokenService;
        this.tokenManager = tokenManager;
        this.userLoginService = userLoginService;
        this.tokenPermissionEvaluator = tokenPermissionEvaluator;
        this.resourcePermissionService = resourcePermissionService;
    }
    
    @GetMapping("token/verify")
    public ResponseEntity<Response<Boolean>> authorizeToken(@RequestParam(required = false) String targetResource, @RequestParam(required = false) String requestedPermission){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication){
            boolean parametersAreNotNullOrEmpty = targetResource != null && !targetResource.isBlank()
                    && requestedPermission != null && !requestedPermission.isBlank();
            if (parametersAreNotNullOrEmpty){
                if (tokenPermissionEvaluator.hasPermission(jwtAuthentication, targetResource, requestedPermission))
                    return new ResponseEntity<>(new Response<>(true,"Authorization succeed"), HttpStatus.OK);
                else
                    return new ResponseEntity<>(new Response<>(false, "Authorization failed"), HttpStatus.UNAUTHORIZED);
            }
            if (jwtAuthentication.isAuthenticated())
                return new ResponseEntity<>(new Response<>(true,"Authorization succeed"), HttpStatus.OK);
            else
                return new ResponseEntity<>(new Response<>(false, "Authorization failed"), HttpStatus.UNAUTHORIZED);

        }
        else
            return new ResponseEntity<>(
                new Response<>(false, "Authentication must made by Jwt toekens"),
                HttpStatus.UNAUTHORIZED
        );
    }


    @GetMapping("token/issue")
    public ResponseEntity<Response<TokenResponseDto>> getToken(@RequestParam String grant){
        Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
        if (userLogin.isPresent()){
            User loggedUser = userLogin.get().getLoggedUser();
            Token token = tokenManager.createUserAccessToken(
                    loggedUser,
                    userLogin.get().getAuthorizedAudiences().toArray(String[]::new),
                    userLogin.get().getAuthorizedScopes().toArray(String[]::new)
            );
            Token refreshToken = tokenManager.createUserRefreshToken(loggedUser, userLogin.get().getAuthorizedAudiences().toArray(String[]::new));
            tokenService.saveBatch(token, refreshToken);
            long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(token.getExp())).toSeconds();
            TokenResponseDto tokenResponseDto = new TokenResponseDto(
                    loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                    loggedUser.getEmail(), token.getSerializedToken(), expiresAt, refreshToken.getSerializedToken(),
                    Constants.BEARER_TOKEN
            );
            return new ResponseEntity<>(new Response<>(tokenResponseDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response<>(null, "Invalid grant"),HttpStatus.BAD_REQUEST);
    }

    @GetMapping("token/refresh")
    @PreAuthorize("hasPermission('REFRESH_TOKEN','read_write')")
    public ResponseEntity<Response<TokenResponseDto>> refreshToken(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthentication jwtAuthentication){
            String origin = request.getHeader(HttpHeaders.ORIGIN);
            if (jwtAuthentication.getUserToken().getAud().stream().anyMatch(audience -> audience.equals(origin))){
                Optional<User> optionalUser = tokenManager.loadUserFromToken(jwtAuthentication.getUserToken());
                if (optionalUser.isEmpty())
                    return new ResponseEntity<>(Response.of("Invalid Token"), HttpStatus.BAD_REQUEST);
                User user = optionalUser.get();
                String[] audiences = jwtAuthentication.getUserToken().getAud().toArray(String[]::new);
                String[] scopes = resourcePermissionService.getPermissionsByUser(user)
                        .stream().map(ResourcePermission::toString).toArray(String[]::new);
                Token newAccessToken = tokenManager.createUserAccessToken(user, audiences, scopes);
                Token newRefreshToken = tokenManager.createUserRefreshToken(user, audiences);
                long expiresAt = Duration.between(Instant.now(), Instant.ofEpochSecond(newAccessToken.getExp())).toSeconds();
                tokenService.saveBatch(newAccessToken, newRefreshToken);
                TokenResponseDto tokenResponseDto = new TokenResponseDto(
                        user.getUserName(), user.getName(), user.getSurname(),
                        user.getEmail(), newAccessToken.getSerializedToken(), expiresAt, newRefreshToken.getSerializedToken(),
                        Constants.BEARER_TOKEN
                );
                return new ResponseEntity<>(Response.of(tokenResponseDto), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(Response.of("Invalid Token"),HttpStatus.BAD_REQUEST);
    }
}

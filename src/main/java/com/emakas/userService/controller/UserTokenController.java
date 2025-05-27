package com.emakas.userService.controller;

import com.emakas.userService.auth.JwtAuthentication;
import com.emakas.userService.dto.LoginModel;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.permissionEvaluators.TokenPermissionEvaluator;
import com.emakas.userService.service.UserLoginService;
import com.emakas.userService.shared.AuthHelper;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.emakas.userService.service.UserService;
import com.emakas.userService.service.UserTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/oauth")
public class UserTokenController {

    private final TokenManager tokenManager;
    private final UserTokenService tokenService;
    private final UserService userService;
    private final UserLoginService userLoginService;
    private final UserTokenService userTokenService;
    private final TokenPermissionEvaluator tokenPermissionEvaluator;

    @Autowired
    public UserTokenController(UserTokenService tokenService, UserService userService, TokenManager tokenManager, UserLoginService userLoginService, UserTokenService userTokenService, TokenPermissionEvaluator tokenPermissionEvaluator){
        this.tokenService = tokenService;
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.userLoginService = userLoginService;
        this.userTokenService = userTokenService;
        this.tokenPermissionEvaluator = tokenPermissionEvaluator;
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
            UserToken userToken = tokenManager.createUserToken(
                    loggedUser, Instant.now().plus(5, ChronoUnit.MINUTES).getEpochSecond(),
                    userLogin.get().getAuthorizedAudiences().toArray(String[]::new),
                    userLogin.get().getAuthorizedScopes().toArray(String[]::new)
            );
            UserToken refreshToken = tokenManager.createRefreshToken(loggedUser);
            userTokenService.save(userToken);
            TokenResponseDto tokenResponseDto = new TokenResponseDto(
                    loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                    loggedUser.getEmail(), userToken.getSerializedToken(), refreshToken.getSerializedToken()
            );
            return new ResponseEntity<>(new Response<>(tokenResponseDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response<>(null, "Invalid grant"),HttpStatus.BAD_REQUEST);
    }


}

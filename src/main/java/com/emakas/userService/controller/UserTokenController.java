package com.emakas.userService.controller;

import com.emakas.userService.dto.LoginModel;
import com.emakas.userService.dto.Response;
import com.emakas.userService.dto.TokenResponseDto;
import com.emakas.userService.model.*;
import com.emakas.userService.service.UserLoginService;
import com.emakas.userService.shared.AuthHelper;
import com.emakas.userService.shared.TokenManager;
import com.emakas.userService.shared.enums.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.emakas.userService.service.UserService;
import com.emakas.userService.service.UserTokenService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/oauth")
public class UserTokenController {

    private final TokenManager tokenManager;
    private final UserTokenService tokenService;
    private final UserService userService;
    private final UserLoginService userLoginService;
    private final UserTokenService userTokenService;

    @Autowired
    public UserTokenController(UserTokenService tokenService, UserService userService, TokenManager tokenManager, UserLoginService userLoginService, UserTokenService userTokenService){
        this.tokenService = tokenService;
        this.userService = userService;
        this.tokenManager = tokenManager;
        this.userLoginService = userLoginService;
        this.userTokenService = userTokenService;
    }
    
    @GetMapping("token/verify")
    public ResponseEntity<Response<Boolean>> authorizeToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String[] audiences){
        /*
        System.out.println(String.format("Authorization: %s",token));
        if (token.startsWith("Bearer ")){
            token = token.substring("Bearer ".length());
            System.out.println(String.format("Authorization: %s",token));
            return switch (tokenManager.verifyJwtToken(token, audiences)){
                case SUCCESS -> new ResponseEntity<>(
                        new Response<>(true),
                        HttpStatus.OK
                );
                case EXPIRED -> new ResponseEntity<>(
                        new Response<>(false, "Token is exceed the date"),
                        HttpStatus.FORBIDDEN
                );
                case FAILED -> new ResponseEntity<>(
                        new Response<>(false, "Token is invalid"),
                        HttpStatus.UNAUTHORIZED
                );
                default -> new ResponseEntity<>(
                        new Response<>(false,"Unknown error!"),
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            };
        }
        */
        return new ResponseEntity<>(
                new Response<>(false, "Jwt token must be supply in Authorization header"),
                HttpStatus.BAD_REQUEST
        );
    }


    @GetMapping("token/issue")
    public ResponseEntity<Response<TokenResponseDto>> getToken(@RequestParam String grant){
        Optional<UserLogin> userLogin = userLoginService.getUserLoginByGrant(grant);
        if (userLogin.isPresent()){
            User loggedUser = userLogin.get().getLoggedUser();
            UserToken userToken = tokenManager.createUserToken(
                    loggedUser, Instant.now().plus(25, ChronoUnit.MINUTES).getEpochSecond(),
                    userLogin.get().getAuthorizedAudiences().toArray(String[]::new),
                    userLogin.get().getAuthorizedScopes().toArray(String[]::new)
            );
            userTokenService.save(userToken);
            TokenResponseDto tokenResponseDto = new TokenResponseDto(
                    loggedUser.getUserName(), loggedUser.getName(), loggedUser.getSurname(),
                    loggedUser.getEmail(), userToken.getSerializedToken()
            );
            return new ResponseEntity<>(new Response<>(tokenResponseDto),HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response<>(null, "Invalid grant"),HttpStatus.BAD_REQUEST);
    }


}

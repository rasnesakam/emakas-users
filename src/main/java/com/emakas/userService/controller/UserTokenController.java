package com.emakas.userService.controller;

import com.emakas.userService.model.*;
import com.emakas.userService.shared.AuthHelper;
import com.emakas.userService.shared.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.emakas.userService.service.UserService;
import com.emakas.userService.service.UserTokenService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "oauth")
public class UserTokenController {

    private TokenManager tokenManager;
    private UserTokenService tokenService;
    private UserService userService;

    @Autowired
    public UserTokenController(UserTokenService tokenService, UserService userService, TokenManager tokenManager){
        this.tokenService = tokenService;
        this.userService = userService;
        this.tokenManager = tokenManager;
    }
    
    @GetMapping("auth")
    public ResponseEntity<Response<Boolean>> authorizeToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam String[] audiences){
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
        return new ResponseEntity<>(
                new Response<>(false, "Jwt token must be supply in Authorization header"),
                HttpStatus.BAD_REQUEST
        );
    }

    @PostMapping("auth")
    public ResponseEntity<Response<String>> getToken(@RequestBody LoginModel login, @RequestParam String[] audiences){
    	User user = userService.getByUserName(login.getUname());
        if (!AuthHelper.checkPassword(user,login))
    	    return new ResponseEntity<>(new Response<>(null,"User credentials did not match"),
                    HttpStatus.UNAUTHORIZED);
        UserToken token = tokenManager.generateUserToken(user, audiences);
        tokenService.save(token);
        Response<String> response = new Response<>(tokenManager.generateJwtToken(token));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

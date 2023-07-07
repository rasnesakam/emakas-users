package com.emakas.userService.controller;

import com.emakas.userService.model.*;
import com.emakas.userService.shared.AuthHelper;
import com.emakas.userService.shared.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
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
    public static ResponseEntity<Response<Boolean>> authorizeToken(){
        return new ResponseEntity<>(new Response<>(false,"work in progress"),
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PostMapping("auth")
    public ResponseEntity<Response<String>> getToken(@RequestBody LoginModel login){
    	User user = userService.getByUserName(login.getUname());
        if (AuthHelper.checkPassword(user,login))
    	    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        UserToken token = tokenManager.generateUserToken(user,login.getAudiences());
        tokenService.save(token);
        Response<String> response = new Response<>(tokenManager.generateJwtToken(token));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

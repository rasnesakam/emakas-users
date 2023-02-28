package com.emakas.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emakas.userService.model.User;
import com.emakas.userService.model.UserDto;
import com.emakas.userService.service.UserService;
import com.emakas.userService.service.UserTokenService;

@RestController
@RequestMapping(path = "oauth")
public class UserTokenController {

    private UserTokenService tokenService;
    private UserService userService;

    @Autowired
    public UserTokenController(UserTokenService service, UserService userService){
        this.tokenService = service;
        this.userService = userService;
    }
    
    

    @PostMapping("auth")
    public ResponseEntity<String> getToken(@RequestBody UserDto userdto){
    	User user = userService.getByUserName(userdto.getUname());
    	// User auth
    	if (user != null) {
    		
    		
    		
    		
    		return new ResponseEntity<String>(
    				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlbWFrYXMubmV0Iiwic3ViIjoiMTFiZTJjZjItNTZhYS00YzY1LWI0MGYtMTI2Y2ZhODZmOTg1IiwibmFtZSI6IkJhdGFyeWEgRMO8bnlhc8SxIn0.KSWU6A_Y6LlCEC0gP48INdYFFE_2p6WBafbjXWyAPqc",
    				HttpStatus.OK);
    	}
    	return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }
}

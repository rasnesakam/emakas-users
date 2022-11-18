package com.emakas.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emakas.userService.model.User;
import com.emakas.userService.service.UserTokenService;

@RestController
@RequestMapping(path = "auth/")
public class UserTokenController {

    private UserTokenService service;

    @Autowired
    public UserTokenController(UserTokenService service){
        this.service = service;
    }

    @PostMapping("")
    public String getToken(@RequestBody User user){
        return "MUST BE JWT";
    }
}

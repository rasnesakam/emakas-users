package com.emakas.userService.model;

import org.springframework.stereotype.Component;

@Component
public class LoginModel {
    private String uname;
    private String password;
    private String[] audiences;

    public String getUname() {
        return uname;
    }

    public String getPassword() {
        return password;
    }

    public String[] getAudiences() {
        return audiences;
    }
}

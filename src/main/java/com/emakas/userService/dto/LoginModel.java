package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class LoginModel {
    private String uname;
    private String password;

    public LoginModel() {
    }

    public LoginModel(String uname, String password) {
        this.uname = uname;
        this.password = password;
    }
}

package com.emakas.userService.model;

import javax.persistence.Column;

import org.springframework.stereotype.Component;

@Component
public class UserDto {

    private String uname;

    private String email;

    private String password;
    
    public String getUname() {
		return uname;
	}
    public String getEmail() {
		return email;
	}
    public String getPassword() {
		return password;
	}
}

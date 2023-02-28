package com.emakas.userService.model;

import javax.persistence.Column;

import org.springframework.stereotype.Component;

@Component
public class UserDto {
	
	@Column(name = "user_name",length = 30)
    private String uname;
    @Column(unique = true, length = 30)
    private String email;
    @Column(length = 64)
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

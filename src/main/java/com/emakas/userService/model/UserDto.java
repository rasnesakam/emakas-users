package com.emakas.userService.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UserDto {

    private String uname;

    private String email;

    private String password;

}

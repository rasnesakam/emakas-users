package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserDto {

    private String userName;

    private String email;

    private String password;

}

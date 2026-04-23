package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserWriteDto {
    private String name;

    private String surname;

    private String userName;

    private String email;

    private String password;
}

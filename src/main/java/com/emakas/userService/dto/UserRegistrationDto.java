package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserRegistrationDto extends UserDto{
    private String name;
    private String surname;

    public UserRegistrationDto(){}

}

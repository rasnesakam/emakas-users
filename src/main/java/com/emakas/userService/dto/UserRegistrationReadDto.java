package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserRegistrationReadDto extends UserReadDto {
    private String name;
    private String surname;

    public UserRegistrationReadDto(){}

}

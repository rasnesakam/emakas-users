package com.emakas.userService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserReadDto {

    private String userName;

    private String name;

    private String surname;

    private String email;


}

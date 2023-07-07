package com.emakas.userService.model;

import org.springframework.stereotype.Component;

@Component
public class UserRegistrationDto extends UserDto{
    private String name;
    private String surname;

    public UserRegistrationDto(){}



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}

package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class UserReadDto {

    private String username;

    private String name;

    private String surname;

    private String email;

    @JsonProperty("full_name")
    private String fullName;
}

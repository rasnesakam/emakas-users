package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Setter
@Component
public class UserReadDto {

    private UUID id;

    @JsonProperty("username")
    private String userName;

    private String name;

    private String surname;

    private String email;

    @JsonProperty("full_name")
    private String fullName;

    private TenantDto tenant;
}

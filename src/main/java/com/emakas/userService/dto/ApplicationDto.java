package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ApplicationDto {
    private String name;
    private String description;
    private String uri;
    @JsonProperty("redirect_uri")
    private String redirectUri;
}

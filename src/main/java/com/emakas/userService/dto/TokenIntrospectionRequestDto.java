package com.emakas.userService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenIntrospectionRequestDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
    @JsonProperty("token_type_hint")
    private String tokenTypeHint;
}

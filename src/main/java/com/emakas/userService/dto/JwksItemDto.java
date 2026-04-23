package com.emakas.userService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwksItemDto {
    private String kty;
    private String kid;
    private String alg;
    private String use;
    private String n;
    private String e;
}

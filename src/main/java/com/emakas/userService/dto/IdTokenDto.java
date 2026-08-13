package com.emakas.userService.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class IdTokenDto {
    // openid scope
    private String issuer;
    private String subject;
    private String audience;
    private long expiresIn;
    private long issuedAt;
    private long authTime;

    // profile scope
    private String name;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickName;
    private String preferredUsername;
    private String picture;
    private String gender;
    private Date birthDate;
    private String zoneInfo;
    private String locale;
    private Long updatedAt;

    // email scope
    private String email;
    private Boolean emailVerified;
}

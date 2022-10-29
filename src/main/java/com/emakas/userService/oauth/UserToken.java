package com.emakas.userService.oauth;

import com.emakas.userService.entity.Entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

public class UserToken extends Entity<UUID> {

    @Column
    private String iss; // Provider of token

    @Column
    private String aud; // Audience of token

    @Column
    private String sub; // Subject of token

    @Column
    private String exp; // Expiration date

    public UserToken(UUID uuid, String iss, String aud, String sub, String exp) {
        super(uuid);
        this.iss = iss;
        this.aud = aud;
        this.sub = sub;
        this.exp = exp;
    }
}

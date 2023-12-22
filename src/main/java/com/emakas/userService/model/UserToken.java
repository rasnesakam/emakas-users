package com.emakas.userService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user_tokens")
public class UserToken extends BaseEntity {

    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @Column
    private String iss; // Provider of token


    /**
     * Represents <b>iss</b> claim for the JWT
     */
    @Column
    private String aud; // Audience of token


    /**
     * Represents <b>sub</b> claim for the JWT
     */
    @Column
    private String sub; // Subject of token


    /**
     * Represents <b>exp</b> claim for the JWT
     */
    @Column
    private String exp; // Expiration date

    @Column
    private String serializedToken;

    public UserToken(String iss, String aud, String sub, String exp, String serializedToken) {
        this.iss = iss;
        this.aud = aud;
        this.sub = sub;
        this.exp = exp;
        this.serializedToken = serializedToken;
    }

    public UserToken(){}

}

package com.emakas.userService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Objects;
import java.util.UUID;

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

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getSerializedToken() {
        return serializedToken;
    }

    public void setSerializedToken(String serializedToken) {
        this.serializedToken = serializedToken;
    }
}

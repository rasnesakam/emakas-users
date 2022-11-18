package com.emakas.userService.model;

import javax.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="user_tokens")
public class UserToken extends BaseEntity {

    @Column
    private String iss; // Provider of token

    @Column
    private String aud; // Audience of token

    @Column
    private String sub; // Subject of token

    @Column
    private String exp; // Expiration date

    public UserToken(String iss, String aud, String sub, String exp) {
        this.iss = iss;
        this.aud = aud;
        this.sub = sub;
        this.exp = exp;
    }

    public UserToken() {

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserToken userToken = (UserToken) o;
        return Objects.equals(getId(), userToken.getId()) && Objects.equals(getIss(), userToken.getIss()) && Objects.equals(getAud(), userToken.getAud()) && Objects.equals(getSub(), userToken.getSub()) && Objects.equals(getExp(), userToken.getExp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIss(), getAud(), getSub(), getExp());
    }
}

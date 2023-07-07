package com.emakas.userService.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "user_name",length = 30)
    private String uname;
    
    @Column(unique = true, length = 30)
    private String email;
    
    @Column(length = 64)
    private String password;
    
    @Column(length = 30)
    private String name;
    
    @Column(length = 30)
    private String surname;

    @Column(length = 10)
    private String passwordSalt;


    public User(){}

    public User(String uname, String email, String password, String name, String surname, String passwordSalt) {
        this.uname = uname;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.passwordSalt = passwordSalt;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
}

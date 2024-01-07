package com.emakas.userService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "user_name", unique = true, length = 30)
    private String userName;
    
    @Column(unique = true, length = 30)
    private String email;
    
    @Column(length = 64)
    private String password;
    
    @Column(length = 30)
    private String name;
    
    @Column(length = 30)
    private String surname;

    public User(){}

    public User(String userName, String email, String password, String name, String surname) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }
}

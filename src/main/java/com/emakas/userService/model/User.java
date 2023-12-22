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

    @Column(name = "user_name", length = 30)
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
}

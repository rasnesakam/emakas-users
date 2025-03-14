package com.emakas.userService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}

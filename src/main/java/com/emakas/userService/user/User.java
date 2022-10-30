package com.emakas.userService.user;

import com.emakas.userService.entity.BaseEntity;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

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

    public User(String uname, String email, String password, String name, String surname) {
        this.uname = uname;
        this.email = email;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public User() {

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getUname(), user.getUname()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(getSurname(), user.getSurname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUname(), getEmail(), getPassword(), getName(), getSurname());
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", uname='" + getUname() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                '}';
    }
}

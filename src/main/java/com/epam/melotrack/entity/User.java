package com.epam.melotrack.entity;

import java.util.Optional;

public class User extends Entity {

    public enum Role{
        ADMIN, USER
    }
    private long userId;
    private String userName;
    private String password;
    private Role role;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role == null ? null : role.toString().toLowerCase();
    }

    public void setRole(String role) {
        this.role = Optional.of(Role.valueOf(role.toUpperCase())).orElse(Role.USER);
    }

}

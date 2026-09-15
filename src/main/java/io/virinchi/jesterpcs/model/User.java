package io.virinchi.jesterpcs.model;

import java.time.LocalDateTime;

public class User {

    private int userId;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String address;
    private boolean emailVerified;
    private String role;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(
            int userId,
            String fullName,
            String username,
            String email,
            String password,
            boolean emailVerified,
            String role) {

        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.role = role;
    }

    public User(
            String fullName,
            String username,
            String email,
            String password) {

        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.emailVerified = false;
        this.role = "USER";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
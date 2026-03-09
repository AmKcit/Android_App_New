package com.example.android_app_new;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String email;
    private String role;
    private boolean active;
    private long lastLogin;
    private long createdAt;

    public User() {
    }

    public User(String uid, String email, String role, boolean active) {
        this.uid = uid;
        this.email = email;
        this.role = role;
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}



package com.example.michelle.driverbuddy;

public class User {

    String userId;
    String password;
    String type;

    public User(String userId, String password, String type) {
        this.userId = userId;
        this.password = password;
        this.type = type;
    }

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

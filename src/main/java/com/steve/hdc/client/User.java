package com.steve.hdc.client;


public class User {
    private int id;
    private String userName;

    public User() {
        this.userName = null;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, int id) {
        this.userName = userName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }
}

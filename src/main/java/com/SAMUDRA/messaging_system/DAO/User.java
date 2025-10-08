package com.SAMUDRA.messaging_system.DAO;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {
    @Id
    private String username;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private String password;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

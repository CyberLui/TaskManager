package com.evtech.taskmanager.entities;

import com.evtech.taskmanager.entities.enuns.Role;
import jakarta.persistence.Id;

public class User {

    @Id
    private Long id;

    private String name;
    private String username;
    private String email;
    private String password;

    private Role role;

    public User(Long id, String email, String password ) {
        this.id = id;
        this.name = email;
        this.username = email;
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_USER;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

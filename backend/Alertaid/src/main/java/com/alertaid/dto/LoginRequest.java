package com.alertaid.dto;

import com.alertaid.model.Role;

public class LoginRequest {
    private String email;
    private String password;
    private Role role; // optional: provided by client to enforce role-based login

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}

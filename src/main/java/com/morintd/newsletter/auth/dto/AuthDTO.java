package com.morintd.newsletter.auth.dto;

import com.morintd.newsletter.user.dao.Role;

import java.util.Objects;

public class AuthDTO {
    private String userId;
    private String email;
    private Role role;

    public AuthDTO() {

    }

    public AuthDTO(String userId, String email, Role role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthDTO authDTO = (AuthDTO) o;
        return Objects.equals(userId, authDTO.userId) && Objects.equals(email, authDTO.email) && role == authDTO.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, role);
    }
}

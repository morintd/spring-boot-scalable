package com.morintd.newsletter.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserToRegisterDTO {
    private String email;
    private String password;

    @Email(message = "Email should be valid")
    public String getEmail() {
        return this.email;
    }

    @Size(min = 8, message = "Password must be at least 8 characters long")
    public String getPassword() {
        return this.password;
    }
}

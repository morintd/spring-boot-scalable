package com.morintd.newsletter.auth.filters;

import com.morintd.newsletter.auth.dto.AuthDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken implements Authentication {
    private final String token;
    private final AuthDTO auth;
    private boolean isAuthenticated;

    public JwtAuthenticationToken(String token, AuthDTO auth) {
        this.token = token;
        this.auth = auth;
        this.isAuthenticated = true;
    }

    @Override
    public String getName() {
        return this.auth.getEmail();
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.auth.getRole().name()));
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public String getDetails() {
        return this.auth.getUserId();
    }

    @Override
    public AuthDTO getPrincipal() { return this.auth; }

    @Override
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.isAuthenticated = isAuthenticated;
    }
}

package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthRepository {
    private static final long ACCESS_TOKEN_EXPIRE_DURATION = 5 * 60 * 1000; // 5 minutes

    @Value("${token.access.secret}")
    private String ACCESS_TOKEN_SECRET;

    public AuthRepository() {}

    public AuthRepository(String accessTokenSecret) {
        this.ACCESS_TOKEN_SECRET = accessTokenSecret;
    }

    public String generateAccessToken(User user) {
        return this.generateAccessToken(new AuthDTO(user.getId(), user.getEmail(), user.getRole()));
    }

    public String generateAccessToken(AuthDTO auth) {
        return Jwts.builder()
                .claim("userId", auth.getUserId())
                .claim("email", auth.getEmail())
                .claim("role", auth.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, ACCESS_TOKEN_SECRET)
                .compact();
    }

    public AuthDTO decodeAccessToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(ACCESS_TOKEN_SECRET).parseClaimsJws(token).getBody();
        return new AuthDTO(claims.get("userId", String.class), claims.get("email", String.class), Role.valueOf(claims.get("role", String.class)));
    }
}

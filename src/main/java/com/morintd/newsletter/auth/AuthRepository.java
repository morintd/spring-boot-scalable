package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.auth.dto.RefreshDTO;
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
    private static final long REFRESH_TOKEN_EXPIRE_DURATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    @Value("${token.access.secret}")
    private String ACCESS_TOKEN_SECRET;

    @Value("${token.refresh.secret}")
    private String REFRESH_TOKEN_SECRET;

    public AuthRepository() {}

    public AuthRepository(String accessTokenSecret, String refreshTokenSecret) {
        this.ACCESS_TOKEN_SECRET = accessTokenSecret;
        this.REFRESH_TOKEN_SECRET = refreshTokenSecret;
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

    public String generateRefreshToken(User user) {
        return this.generateRefreshToken(new RefreshDTO(user.getId(), user.getRefreshId()));
    }

    public String generateRefreshToken(RefreshDTO auth) {
        return Jwts.builder()
                .claim("userId", auth.getUserId())
                .claim("refreshId", auth.getRefreshId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, REFRESH_TOKEN_SECRET)
                .compact();
    }

    public RefreshDTO decodeRefreshToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(REFRESH_TOKEN_SECRET).parseClaimsJws(token).getBody();
        return new RefreshDTO(claims.get("userId", String.class), claims.get("refreshId", String.class));
    }
}

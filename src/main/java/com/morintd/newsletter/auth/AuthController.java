package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.RefreshDTO;
import com.morintd.newsletter.auth.dto.UserToLoginDTO;
import com.morintd.newsletter.auth.dto.UserToRegisterDTO;
import com.morintd.newsletter.common.services.IDGenerator;
import com.morintd.newsletter.user.UserRepository;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final int ACCESS_TOKEN_EXPIRE_DURATION = 5 * 60; // 5 minutes
    private static final int REFRESH_TOKEN_EXPIRE_DURATION = 7 * 24 * 60 * 60; // 7 days

    private final IDGenerator idGenerator;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(IDGenerator idGenerator, AuthRepository authRepository,UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.idGenerator = idGenerator;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    void login(@RequestBody UserToLoginDTO userToLogin, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userToLogin.getEmail(), userToLogin.getPassword()));

            User user = (User) authentication.getPrincipal();

            user.setRefreshId(this.idGenerator.generate());
            this.userRepository.update(user);

            this.setAuthCookies(response, user);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.CREATED)
    void refreshToken(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        RefreshDTO refresh = this.authRepository.decodeRefreshToken(refreshToken);
        User user = userRepository.findByRefresh(refresh.getUserId(), refresh.getRefreshId()).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token."));

        user.setRefreshId(this.idGenerator.generate());
        this.userRepository.update(user);

        this.setAuthCookies(response, user);
    }

    @PostMapping("/register")
    void register(@Valid @RequestBody UserToRegisterDTO userToRegister, HttpServletResponse response) {
        try {
            User user = new User(this.idGenerator.generate(), userToRegister.getEmail(), this.passwordEncoder.encode(userToRegister.getPassword()), Role.ROLE_USER, this.idGenerator.generate());
            this.userRepository.create(user);

            this.setAuthCookies(response, user);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch(DataIntegrityViolationException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.CREATED)
    void logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }

    private void setAuthCookies(HttpServletResponse response, User user) {
        String accessToken = this.authRepository.generateAccessToken(user);
        String refreshToken = this.authRepository.generateRefreshToken(user);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(ACCESS_TOKEN_EXPIRE_DURATION);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_EXPIRE_DURATION);

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);
    }
}

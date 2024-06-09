package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.UserToLoginDTO;
import com.morintd.newsletter.auth.dto.UserToRegisterDTO;
import com.morintd.newsletter.common.services.IDGenerator;
import com.morintd.newsletter.user.UserRepository;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
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
    @ResponseStatus(HttpStatus.CREATED)
    String createArticle(@RequestBody UserToLoginDTO userToLogin) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userToLogin.getEmail(), userToLogin.getPassword()));
        User user = userRepository.findByEmail(userToLogin.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        return this.authRepository.generateAccessToken(user);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    String register(@RequestBody UserToRegisterDTO userToRegister) {
        User user = new User(this.idGenerator.generate(), userToRegister.getEmail(), this.passwordEncoder.encode(userToRegister.getPassword()), Role.ROLE_USER);
        this.userRepository.create(user);

        return this.authRepository.generateAccessToken(user);
    }
}

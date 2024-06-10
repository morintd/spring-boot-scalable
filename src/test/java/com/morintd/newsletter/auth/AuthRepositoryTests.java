package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.auth.dto.RefreshDTO;
import com.morintd.newsletter.user.dao.Role;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthRepositoryTests {
    AuthRepository authRepository = new AuthRepository("testing-secret", "testing-refresh-secret");

    @Test
    void shouldGenerateDecodeAccessJWT() {
        AuthDTO expected = new AuthDTO("1", "morin.td@gmail.com", Role.ROLE_USER);
        String jwt = this.authRepository.generateAccessToken(expected);
        AuthDTO actual = this.authRepository.decodeAccessToken(jwt);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateDecodeRefreshJWT() {
        RefreshDTO expected = new RefreshDTO("user-id", "refresh-id");
        String jwt = this.authRepository.generateRefreshToken(expected);
        RefreshDTO actual = this.authRepository.decodeRefreshToken(jwt);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldFailAccessWithWrongSecret() {
        AuthDTO expected = new AuthDTO("1", "email", Role.ROLE_USER);
        String jwt = this.authRepository.generateAccessToken(expected);

        AuthRepository wrongRepository = new AuthRepository("wrong-access-secret", "wrong-refresh-secret");
        assertThrows(SignatureException.class, () -> wrongRepository.decodeAccessToken(jwt));
    }

    @Test
    void shouldFailRefreshWithWrongSecret() {
        RefreshDTO expected = new RefreshDTO("user-id", "refresh-id");
        String jwt = this.authRepository.generateRefreshToken(expected);

        AuthRepository wrongRepository = new AuthRepository("wrong-access-secret", "wrong-refresh-secret");
        assertThrows(SignatureException.class, () -> wrongRepository.decodeRefreshToken(jwt));
    }
}

package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.user.dao.Role;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class AuthRepositoryTests {
    AuthRepository authRepository = new AuthRepository("testing-secret");

    @Test
    void shouldGenerateDecodeJWT() throws Exception {
        AuthDTO expected = new AuthDTO("1", "morin.td@gmail.com", Role.ROLE_USER);
        String jwt = this.authRepository.generateAccessToken(expected);
        AuthDTO actual = this.authRepository.decodeAccessToken(jwt);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldFailWithWrongSecret() throws Exception {
        AuthDTO expected = new AuthDTO("1", "email", Role.ROLE_USER);
        String jwt = this.authRepository.generateAccessToken(expected);

        AuthRepository wrongRepository = new AuthRepository("wrong-access-token");
        assertThrows(SignatureException.class, () -> wrongRepository.decodeAccessToken(jwt));
    }
}

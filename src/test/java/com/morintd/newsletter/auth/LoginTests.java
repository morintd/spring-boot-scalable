package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.auth.dto.RefreshDTO;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import com.morintd.newsletter.user.dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:login" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private UserDAO userDAO;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user = new User("1", "user@company.com", passwordEncoder.encode("password"), Role.ROLE_USER, "1");

    @BeforeAll
    public void initiAll() {
        this.userDAO.save(user);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class OnSuccess {
        String accessToken;
        String refreshToken;

        @BeforeAll
        public void initAll() throws Exception {
            String body = "{" +
                    "\"email\":\"" + user.getEmail() + "\"," +
                    "\"password\":\"password\"" +
                    "}";

            MvcResult result = mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                            .andExpect(status().isCreated()).andReturn();

            accessToken = result.getResponse().getCookie("accessToken").getValue();
            refreshToken = result.getResponse().getCookie("refreshToken").getValue();
        }

        @Test
        void shouldReturnAccessToken() throws Exception {
            AuthDTO actual = authRepository.decodeAccessToken(accessToken);
            AuthDTO expected = new AuthDTO(actual.getUserId(), "user@company.com", Role.ROLE_USER);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldReturnRefreshToken() throws Exception {
            RefreshDTO actual = authRepository.decodeRefreshToken(refreshToken);
            RefreshDTO expected = new RefreshDTO(user.getId(), actual.getRefreshId());

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    void shouldReturnErrorIfCredentialsIncorrect() throws Exception {
        String body = "{" +
                "\"email\":\"" + user.getEmail() + "\"," +
                "\"password\":\"wrong-password\"" +
                "}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isBadRequest());
    }
}

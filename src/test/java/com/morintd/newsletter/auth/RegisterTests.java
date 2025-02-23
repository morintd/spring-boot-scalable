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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:register" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegisterTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AuthRepository authRepository;

    @BeforeAll
    public void initiAll() {
        User user = new User("1", "already-exist@company.com", "password", Role.ROLE_USER, "1");
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
                    "\"email\":\"user@company.com\"," +
                    "\"password\":\"password\"" +
                    "}";

            MvcResult result = mockMvc.perform(post("/auth/register")
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
            RefreshDTO expected = new RefreshDTO(actual.getUserId(), actual.getRefreshId());

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldCreateUser() {
            Optional<User> actual = userDAO.findByEmail("user@company.com");

            if(actual.isPresent()) {
                User expected = new User(actual.get().getId(), "user@company.com", actual.get().getPassword(), Role.ROLE_USER, actual.get().getRefreshId());
                assertThat(actual.get()).isEqualTo(expected);
            } else {
                fail("User wasn't created");
            }
        }
    }

    @Test
    void shouldReturnErrorIfEmailIsTaken() throws Exception {
        String body = "{" +
                "\"email\":\"already-exist@company.com\"," +
                "\"password\":\"password\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnErrorIfEmailIsInvalid() throws Exception {
        String body = "{" +
                "\"email\":\"bad-email\"," +
                "\"password\":\"password\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnErrorIfPasswordIsTooShort() throws Exception {
        String body = "{" +
                "\"email\":\"password-too-short@company.com\"," +
                "\"password\":\"abc\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isBadRequest());
    }
}

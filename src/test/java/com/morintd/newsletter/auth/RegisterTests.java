package com.morintd.newsletter.auth;

import com.morintd.newsletter.auth.dto.AuthDTO;
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class OnSuccess {
        String response;

        @BeforeAll
        public void initAll() throws Exception {
            String body = "{" +
                    "\"email\":\"user@company.com\"," +
                    "\"password\":\"password\"" +
                    "}";

            response = mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        }

        @Test
        void shouldReturnAccessToken() throws Exception {
            AuthDTO actual = authRepository.decodeAccessToken(response);
            AuthDTO expected = new AuthDTO(actual.getUserId(), "user@company.com", Role.ROLE_USER);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void shouldCreateUser() {
            Optional<User> actual = userDAO.findByEmail("user@company.com");

            if(actual.isPresent()) {
                User expected = new User(actual.get().getId(), "user@company.com", actual.get().getPassword(), Role.ROLE_USER);
                assertThat(actual.get()).isEqualTo(expected);
            } else {
                fail("User wasn't created");
            }
        }
    }
}

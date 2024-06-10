package com.morintd.newsletter.article;

import com.jayway.jsonpath.JsonPath;
import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
import com.morintd.newsletter.auth.AuthRepository;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import com.morintd.newsletter.user.dao.UserDAO;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.skyscreamer.jsonassert.JSONAssert;
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

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:createarticle" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateArticleTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ArticleDAO articleDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private AuthRepository authRepository;

    private final User user = new User("1", "user@company.com", "password", Role.ROLE_USER, "1");
    private final User admin = new User("2", "admin@company.com", "password", Role.ROLE_ADMIN, "2");

    @BeforeAll
    public void initiAll() {
        this.userDAO.save(user);
        this.userDAO.save(admin);

        Article article = new Article("1", "already-exist", "already-exist", "content", admin.getId());
        this.articleDAO.save(article);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class OnSuccess {
        String response;

        @BeforeAll
        public void initAll() throws Exception {
            String accessToken = authRepository.generateAccessToken(admin);
            Cookie cookie = new Cookie("accessToken", accessToken);

            String body = "{" +
                "\"title\":\"article-title\"," +
                "\"content\":\"article-content\"" +
            "}";

            response = mockMvc.perform(post("/article")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        }

        @Test
        void shouldReturnCreatedArticle() throws Exception {
            String id = JsonPath.read(response, "$.id");

            String expected = "{" +
                "id:" + id + "," +
                "title:\"article-title\"," +
                "slug:\"article-title\"," +
                "content:\"article-content\"," +
                "author:" + admin.getEmail() +
            "}";

            JSONAssert.assertEquals(expected, response, true);
        }

        @Test
        void shouldCreateArticle() {
            String id = JsonPath.read(response, "$.id");

            Optional<Article> actual = articleDAO.findById(id);
            Article expected = new Article(id, "article-title", "article-title", "article-content", admin.getId());

            if(actual.isPresent()) {
                assertThat(actual.get()).isEqualTo(expected);
            } else {
                fail("Article wasn't created");
            }
        }
    }

    @Test
    void shouldReturnErrorIfTitleIsAlreadyTaken() throws Exception {
        String accessToken = authRepository.generateAccessToken(admin);
        Cookie cookie = new Cookie("accessToken", accessToken);

        String body = "{" +
                "\"title\":\"already-exist\"," +
                "\"content\":\"article-content\"" +
                "}";


        mockMvc.perform(post("/article")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnErrorIfLackRole() throws Exception {
        String accessToken = authRepository.generateAccessToken(user);
        Cookie cookie = new Cookie("accessToken", accessToken);

        String body = "{" +
                "\"title\":\"article-title\"," +
                "\"content\":\"article-content\"" +
                "}";

        mockMvc.perform(post("/article")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isForbidden());
    }
}

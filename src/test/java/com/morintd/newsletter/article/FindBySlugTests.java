package com.morintd.newsletter.article;

import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
import com.morintd.newsletter.user.dao.Role;
import com.morintd.newsletter.user.dao.User;
import com.morintd.newsletter.user.dao.UserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:findbyslug" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FindBySlugTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ArticleDAO articleDAO;
    @Autowired
    private UserDAO userDAO;

    @BeforeAll
    public void initAll() {
        User admin = new User("1", "admin@company.com", "password", Role.ROLE_ADMIN, "1");
        Article article = new Article("1", "title", "slug", "content", admin.getId());

        this.userDAO.save(admin);
        this.articleDAO.save(article);
    }

    @Test
    void shouldReturnArticle() throws Exception {
        String expected = "{" +
            "\"id\":\"1\"," +
            "\"title\":\"title\"," +
            "\"slug\":\"slug\"," +
            "\"content\":\"content\"," +
            "\"author\":\"admin@company.com\"" +
        "}";

        mockMvc.perform(get("/article/slug"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }
}

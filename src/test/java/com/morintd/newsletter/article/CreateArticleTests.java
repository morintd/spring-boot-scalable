package com.morintd.newsletter.article;

import com.jayway.jsonpath.JsonPath;
import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateArticleTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleDAO articleDAO;


    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class OnSuccess {
        String response;

        @BeforeAll
        public void initAll() throws Exception {
            String body = "{" +
                "\"title\":\"article-title\"," +
                "\"content\":\"article-content\"" +
            "}";

            response = mockMvc.perform(post("/article")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        }

        @Test
        void shouldReturnCreatedArticle() throws Exception {
            String id = JsonPath.read(response, "$.id");

            String expected = "{" +
                "id:" + id + "," +
                "\"title\":\"article-title\"," +
                "\"slug\":\"article-title\"," +
                "\"content\":\"article-content\"" +
            "}";

            JSONAssert.assertEquals(expected, response, true);
        }

        @Test
        void ShouldCreateArticle() {
            String id = JsonPath.read(response, "$.id");

            Optional<Article> actual = articleDAO.findById(id);
            Article expected = new Article(id, "article-title", "article-title", "article-content");

            if(actual.isPresent()) {
                assertThat(actual.get()).isEqualTo(expected);
            } else {
                fail("Article wasn't created");
            }
        }
    }
}

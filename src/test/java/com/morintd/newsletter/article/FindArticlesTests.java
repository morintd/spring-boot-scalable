package com.morintd.newsletter.article;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.morintd.newsletter.article.services.dao.Article;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import com.morintd.newsletter.article.services.dao.ArticleDAO;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:findarticles" })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FindArticlesTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleDAO articleDAO;

    @BeforeAll
    public void initAll() {
        List<Article> articles = Stream.iterate(1, i -> i + 1).limit(5).map(i -> {
            return new Article(i.toString(), "title" + i, "slug" + i, "content" + i);
        }).toList();

        this.articleDAO.saveAll(articles);
    }

    @Test
    void shouldReturnPage() throws Exception {
        String expected = "{" +
            "content:[" +
                "{\"id\":\"1\",\"title\":\"title1\",\"slug\":\"slug1\",\"content\":\"content1\"}," +
                "{\"id\":\"2\",\"title\":\"title2\",\"slug\":\"slug2\",\"content\":\"content2\"}," +
                "{\"id\":\"3\",\"title\":\"title3\",\"slug\":\"slug3\",\"content\":\"content3\"}," +
                "{\"id\":\"4\",\"title\":\"title4\",\"slug\":\"slug4\",\"content\":\"content4\"}," +
                "{\"id\":\"5\",\"title\":\"title5\",\"slug\":\"slug5\",\"content\":\"content5\"}]," +
            "page:{" +
                "size:5," +
                "number:0," +
                "totalElements:5," +
                "totalPages:1" +
            "}" +
        "}";

        mockMvc.perform(get("/article?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }
}

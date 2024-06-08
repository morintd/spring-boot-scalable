package com.morintd.newsletter;

import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class NewsletterApplication {
    private ArticleDAO articleDao;

    public static void main(String[] args) {
        SpringApplication.run(NewsletterApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(String[] args) {
        return runner -> {
            List<Article> articles = this.articleDao.findAll();

//            if (articles.isEmpty()) {
//                List<Article> seed = new ArrayList<>();
//                seed.add(new Article("title 1", "slug1", "content1"));
//                seed.add(new Article("title 2", "slug2", "content 2"));
//
//                this.articleDao.saveAll(seed);
//            }
            System.out.println(articles);
        };
    }

    @Autowired
    public void setArticleRepository(ArticleDAO articleDAO) {
        this.articleDao = articleDAO;
    }
}

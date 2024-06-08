package com.morintd.newsletter.article.services;

import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepository {
    private final ArticleDAO dao;

    public ArticleRepository(ArticleDAO dao) {
        this.dao = dao;
    }

    public PagedModel<Article> findByPage(int page) {
        return new PagedModel<>(this.dao.findAll(PageRequest.of(page, 5)));
    }

    public Article findBySlug(String slug) {
        return this.dao.findBySlug(slug);
    }

    public Article create(Article article) {
        return this.dao.save(article);
    }
}

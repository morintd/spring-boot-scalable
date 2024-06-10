package com.morintd.newsletter.article.services;

import com.morintd.newsletter.article.models.PublicArticle;
import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.article.services.dao.ArticleDAO;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;

@Service
public class ArticleRepository {
    private final ArticleDAO dao;

    public ArticleRepository(ArticleDAO dao) {
        this.dao = dao;
    }

    public PagedModel<PublicArticle> findByPage(int page) {
        return new PagedModel<>(this.dao.findAllPublicArticles(PageRequest.of(page, 5)));
    }

    public Article findBySlug(String slug) {
        return this.dao.findBySlug(slug);
    }

    public Article create(Article article) {
        return this.dao.save(article);
    }
}

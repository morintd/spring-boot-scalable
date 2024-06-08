package com.morintd.newsletter.article.services.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDAO extends JpaRepository<Article, String> {
    Article findBySlug(String slug);
}
package com.morintd.newsletter.article.services.dao;

import com.morintd.newsletter.article.models.PublicArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDAO extends JpaRepository<Article, String> {
    Article findBySlug(String slug);
    @Query("SELECT new com.morintd.newsletter.article.models.PublicArticle(a) FROM Article a")
    Page<PublicArticle> findAllPublicArticles(Pageable pageable);
}
package com.morintd.newsletter.article.models;

import com.morintd.newsletter.article.services.dao.Article;

import java.util.Objects;

public class PublicArticle {
    private String id;
    private String title;
    private String slug;
    private String content;
    private String author;

    public PublicArticle() {

    }

    public PublicArticle(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.slug = article.getSlug();
        this.content = article.getContent();
        this.author = article.getAuthor().getUsername();
    }

    public PublicArticle(String id, String title, String slug, String content, String author) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicArticle that = (PublicArticle) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(slug, that.slug) && Objects.equals(content, that.content) && Objects.equals(author, that.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, slug, content, author);
    }

    @Override
    public String toString() {
        return "CreateArticleResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

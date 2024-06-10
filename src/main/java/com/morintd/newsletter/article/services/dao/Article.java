package com.morintd.newsletter.article.services.dao;

import com.morintd.newsletter.user.dao.User;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Column(name = "title", unique = true)
    private String title;
    @Column(name = "slug", unique = true)
    private String slug;
    @Column(name = "content")
    private String content;
    @Column(name = "author_id")
    private String authorId;
    @ManyToOne
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;

    public Article() {

    }

    public Article(String id, String title, String slug, String content, String authorId) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.authorId = authorId;
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

    public String getAuthorId() { return authorId; }

    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public User getAuthor() { return author; }

    public void setAuthor(User author) { this.author = author; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id) && Objects.equals(title, article.title) && Objects.equals(slug, article.slug) && Objects.equals(content, article.content) && Objects.equals(authorId, article.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, slug, content, authorId);
    }

    @Override
    public String toString() {
        return "Article{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", content='" + content + '\'' +
                ", authorId='" + authorId + '\'' +
                '}';
    }
}
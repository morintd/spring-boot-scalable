package com.morintd.newsletter.article;

import com.morintd.newsletter.article.dto.ArticleToCreateDTO;
import com.morintd.newsletter.article.services.ArticleRepository;
import com.morintd.newsletter.article.services.SlugGenerator;
import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.common.services.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleRepository articleRepository;
    private final SlugGenerator slugGenerator;
    private final IDGenerator idGenerator;

    @Autowired
    public ArticleController(ArticleRepository articleRepository, SlugGenerator slugGenerator, IDGenerator idGenerator) {
        this.articleRepository = articleRepository;
        this.slugGenerator = slugGenerator;
        this.idGenerator = idGenerator;
    }

    @GetMapping
    PagedModel<Article> findArticlesByPage(@RequestParam("page") int page) {
        return this.articleRepository.findByPage(page - 1);
    }

    @GetMapping("/{slug}")
    Article findBySlug(@PathVariable String slug) {
        Article article = this.articleRepository.findBySlug(slug);

        if (article == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return article;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Article createArticle(@RequestBody ArticleToCreateDTO articleToCreate) {
        Article article = new Article(this.idGenerator.generate(), articleToCreate.getTitle(), this.slugGenerator.generate(articleToCreate.getTitle()), articleToCreate.getContent());
        return this.articleRepository.create(article);
    }
}

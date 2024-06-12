package com.morintd.newsletter.article;

import com.morintd.newsletter.article.dto.ArticleToCreateDTO;
import com.morintd.newsletter.article.models.PublicArticle;
import com.morintd.newsletter.article.services.ArticleRepository;
import com.morintd.newsletter.article.services.SlugGenerator;
import com.morintd.newsletter.article.services.dao.Article;
import com.morintd.newsletter.auth.dto.AuthDTO;
import com.morintd.newsletter.common.services.IDGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    PagedModel<PublicArticle> findArticlesByPage(@RequestParam("page") int page) {
        return this.articleRepository.findByPage(page);
    }

    @GetMapping("/{slug}")
    PublicArticle findBySlug(@PathVariable String slug) {
        Article article = this.articleRepository.findBySlug(slug);

        if (article == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return new PublicArticle(article);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    PublicArticle createArticle(@RequestBody ArticleToCreateDTO articleToCreate, HttpServletResponse response) {
        try {
            AuthDTO auth = (AuthDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Article article = new Article(this.idGenerator.generate(), articleToCreate.getTitle(), this.slugGenerator.generate(articleToCreate.getTitle()), articleToCreate.getContent(), auth.getUserId());
            this.articleRepository.create(article);

            response.setStatus(HttpServletResponse.SC_CREATED);
            return new PublicArticle(
                    article.getId(),
                    article.getTitle(),
                    article.getSlug(),
                    article.getContent(),
                    auth.getEmail()
            );
        }
        catch(DataIntegrityViolationException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }
}

package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.ArticleCreateDTO;
import com.julio_compressor.myblog.dto.ArticleDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.service.ArticleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping ("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        try {
            List<ArticleDTO> articles = articleService.getAllArticles();
            return ResponseEntity.ok(articles);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), "NOT_FOUND");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.getArticleById(id);
            return ResponseEntity.ok(article);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus("Article not found with id: " + id, "NOT_FOUND");
        }
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleCreateDTO articleCreateDTO) {
        try {
            ArticleDTO createdArticle = articleService.createArticle(articleCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), e.getStatus());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleCreateDTO articleUpdateDTO
    ) {
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleUpdateDTO);
            return ResponseEntity.ok(updatedArticle);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), e.getStatus());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus("Article not found with id: " + id, "NOT_FOUND");
        }
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<ArticleDTO>> searchByTitle(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
            }
            List<ArticleDTO> articles = articleService.searchByTitle(query);
            return ResponseEntity.ok(articles);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), e.getStatus());
        }
    }

    @GetMapping("/search/content")
    public ResponseEntity<List<ArticleDTO>> searchByContent(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
            }
            List<ArticleDTO> articles = articleService.searchByContent(query);
            return ResponseEntity.ok(articles);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), e.getStatus());
        }
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<ArticleDTO>> getArticlesCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ) {
        try {
            if (date == null) {
                throw new ExceptionStatus("Date parameter cannot be null", "BAD_REQUEST");
            }
            List<ArticleDTO> articles = articleService.getArticlesCreatedAfter(date);
            return ResponseEntity.ok(articles);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), e.getStatus());
        }
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ArticleDTO>> getLatestArticles() {
        try {
            List<ArticleDTO> articles = articleService.getLatestArticles();
            return ResponseEntity.ok(articles);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), "NOT_FOUND");
        }
    }
}

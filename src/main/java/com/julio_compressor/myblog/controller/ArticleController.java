package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.ArticleCreateDTO;
import com.julio_compressor.myblog.dto.ArticleDTO;
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
        List<ArticleDTO> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        ArticleDTO article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleCreateDTO articleCreateDTO) {
        ArticleDTO createdArticle = articleService.createArticle(articleCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleCreateDTO articleUpdateDTO
    ) {
        ArticleDTO updatedArticle = articleService.updateArticle(id, articleUpdateDTO);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<ArticleDTO>> searchByTitle(@RequestParam String query) {
        List<ArticleDTO> articles = articleService.searchByTitle(query);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search/content")
    public ResponseEntity<List<ArticleDTO>> searchByContent(@RequestParam String query) {
        List<ArticleDTO> articles = articleService.searchByContent(query);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<ArticleDTO>> getArticlesCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ) {
        List<ArticleDTO> articles = articleService.getArticlesCreatedAfter(date);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ArticleDTO>> getLatestArticles() {
        List<ArticleDTO> articles = articleService.getLatestArticles();
        return ResponseEntity.ok(articles);
    }
}

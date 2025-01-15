package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.ArticleDTO;
import com.julio_compressor.myblog.exceptions.ExeptionStatus;
import com.julio_compressor.myblog.model.*;
import com.julio_compressor.myblog.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final AuthorRepository authorRepository;
    private final ArticleAuthorRepository articleAuthorRepository;

    public ArticleController(
            ArticleRepository articleRepository,
            CategoryRepository categoryRepository,
            ImageRepository imageRepository,
            AuthorRepository authorRepository,
            ArticleAuthorRepository articleAuthorRepository
    ) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.authorRepository = authorRepository;
        this.articleAuthorRepository = articleAuthorRepository;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ArticleDTO> articleDTOS = articles.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(articleDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(article));
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody Article article) throws Exception {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (isTitleExist(article)) {
            throw new ExeptionStatus("Title already exist", "CONFLICT");
        }

        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        // Gestion de la catégorie
        if (article.getCategory() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId())
                    .orElseThrow(() -> new ExeptionStatus("Category not found", "BAD_REQUEST"));
            article.setCategory(category);
        }

        // Gestion des images
        if (article.getImages() != null && !article.getImages().isEmpty()) {
            List<Image> validImages = validateAndSaveImages(article.getImages());
            article.setImages(validImages);
        }

        // Gestion des auteurs
        List<ArticleAuthor> articleAuthors = new ArrayList<>();
        if (article.getArticleAuthors() != null) {
            for (ArticleAuthor articleAuthor : article.getArticleAuthors()) {
                Author author = authorRepository.findById(articleAuthor.getAuthor().getId())
                        .orElseThrow(() -> new ExeptionStatus("Author not found", "BAD_REQUEST"));

                ArticleAuthor newArticleAuthor = new ArticleAuthor();
                newArticleAuthor.setAuthor(author);
                newArticleAuthor.setContribution(articleAuthor.getContribution());
                newArticleAuthor.setArticle(article);
                articleAuthors.add(newArticleAuthor);
            }
        }

        Article savedArticle = articleRepository.save(article);

        // Sauvegarde des relations article-auteur
        if (!articleAuthors.isEmpty()) {
            articleAuthors.forEach(aa -> aa.setArticle(savedArticle));
            articleAuthorRepository.saveAll(articleAuthors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedArticle));
    }

    @PutMapping("{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody Article articleDetails) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        if (isTitleExist(article)) {
            throw new ExeptionStatus("Title already exist", "CONFLICT");
        }
        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        article.setUpdatedAt(LocalDateTime.now());
        if (articleDetails.getCategory() != null) {
            Category category = categoryRepository.findById(articleDetails.getCategory().getId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().body(null);
            }
            article.setCategory(category);
        }
        if (articleDetails.getImages() != null) {
            List<Image> validImages = new ArrayList<>();
            for (Image image : articleDetails.getImages()) {
                if (image.getId() != null) {
                    Image existingImage = imageRepository.findById(image.getId()).orElse(null);
                    if (existingImage != null) {
                        validImages.add(existingImage);
                    } else {
                        return ResponseEntity.badRequest().build();
                    }
                } else {
                    Image savedImage = imageRepository.save(image);
                    validImages.add(savedImage);
                }
            }
            article.setImages(validImages);
        } else {
            article.getImages().clear();
        }
        if (articleDetails.getArticleAuthors() != null) {
            // Supprime les anciennes relations
            articleAuthorRepository.deleteByArticle(article);

            List<ArticleAuthor> newArticleAuthors = articleDetails.getArticleAuthors().stream()
                    .map(aa -> {
                        Author author = authorRepository.findById(aa.getAuthor().getId())
                                .orElseThrow(() -> new ExeptionStatus("Author not found", "BAD_REQUEST"));

                        ArticleAuthor newAa = new ArticleAuthor();
                        newAa.setAuthor(author);
                        newAa.setArticle(article);
                        newAa.setContribution(aa.getContribution());
                        return newAa;
                    })
                    .collect(Collectors.toList());

            articleAuthorRepository.saveAll(newArticleAuthors);
        }
        Article savedArticle = articleRepository.save(article);
        return ResponseEntity.ok(convertToDTO(savedArticle));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Article> deleteArticle(@PathVariable Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        articleRepository.delete(article);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-title")
    public ResponseEntity<List<Article>> getArticlesByTitle(@RequestParam String query) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Article> articles = articleRepository.findByTitle(query);
        if (articles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-content")
    public ResponseEntity<List<Article>> getArticlesByContent(@RequestParam String query) {
        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Article> articles = articleRepository.findByContent(query);
        if (articles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/created-after")
    public ResponseEntity<List<Article>> getCreatedAfter(@RequestParam LocalDateTime date) {
        if (date == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Article> articles = articleRepository.findByCreatedAtAfter(date);
        if (articles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/latest-articles")
    public ResponseEntity<List<Article>> getFiveLatestArticle() {
        Pageable pageable = PageRequest.of(0, 5);

        List<Article> latestArticles = articleRepository.findTop5ByOrderByCreatedAtDesc(pageable);
        if (latestArticles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestArticles);
    }
    private Boolean isTitleExist(Article article) {
        List <Article> existingArticles= articleRepository.findByTitle(article.getTitle());
        if (!existingArticles.isEmpty()) {
            return true;
        }
        return false;
    }
    private List<Image> validateAndSaveImages(List<Image> images) {
        List<Image> validImages = new ArrayList<>();
        for (Image image : images) {
            if (image.getId() != null) {
                Image existingImage = imageRepository.findById(image.getId())
                        .orElseThrow(() -> new ExeptionStatus("Image not found", "BAD_REQUEST"));
                validImages.add(existingImage);
            } else {
                Image savedImage = imageRepository.save(image);
                validImages.add(savedImage);
            }
        }
        return validImages;
    }
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        if (article.getCategory() != null) {
            articleDTO.setCategoryName(article.getCategory().getName());
        }
        if (article.getImages() != null) {
            articleDTO.setImageUrls(article.getImages().stream().map(Image::getUrl).collect(Collectors.toList()));
        }
        if (article.getArticleAuthors() != null) {
            List<String> authors = article.getArticleAuthors().stream()
                    .map(aa -> aa.getAuthor().getFirstName() + " " + aa.getAuthor().getLastName())
                    .collect(Collectors.toList());
            articleDTO.setAuthors(authors);

            List<String> contributions = article.getArticleAuthors().stream()
                    .map(ArticleAuthor::getContribution)
                    .collect(Collectors.toList());
            articleDTO.setAuthorContributions(contributions);
        }

        return articleDTO;
    }
}

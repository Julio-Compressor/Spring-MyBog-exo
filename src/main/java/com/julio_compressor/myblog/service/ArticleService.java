package com.julio_compressor.myblog.service;

import com.julio_compressor.myblog.dto.ArticleCreateDTO;
import com.julio_compressor.myblog.dto.ArticleDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.model.*;
import com.julio_compressor.myblog.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final ImageRepository imageRepository;
    private final ArticleAuthorRepository articleAuthorRepository;

    public ArticleService(
            ArticleRepository articleRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository,
            ImageRepository imageRepository,
            ArticleAuthorRepository articleAuthorRepository
    ) {
        this.articleRepository = articleRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.imageRepository = imageRepository;
        this.articleAuthorRepository = articleAuthorRepository;
    }

    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            throw new ExceptionStatus("No articles found", "NOT_FOUND");
        }
        return articles.stream()
                .map(ArticleDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Article not found with id: " + id, "NOT_FOUND"));
        return ArticleDTO.mapFromEntity(article);
    }

    @Transactional
    public ArticleDTO createArticle(ArticleCreateDTO articleCreateDTO) {
        // Validation du titre
        if (articleCreateDTO.title() == null || articleCreateDTO.title().trim().isEmpty()) {
            throw new ExceptionStatus("Title cannot be empty", "BAD_REQUEST");
        }
        if (isTitleExist(articleCreateDTO.title())) {
            throw new ExceptionStatus("Title already exists", "CONFLICT");
        }

        Article article = new Article();
        article.setTitle(articleCreateDTO.title());
        article.setContent(articleCreateDTO.content());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        if (articleCreateDTO.categoryId() != null) {
            Category category = categoryRepository.findById(articleCreateDTO.categoryId())
                    .orElseThrow(() -> new ExceptionStatus("Category not found", "BAD_REQUEST"));
            article.setCategory(category);
        }

        if (articleCreateDTO.imageIds() != null && !articleCreateDTO.imageIds().isEmpty()) {
            List<Image> images = imageRepository.findAllById(articleCreateDTO.imageIds());
            if (images.size() != articleCreateDTO.imageIds().size()) {
                throw new ExceptionStatus("Some images were not found", "BAD_REQUEST");
            }
            article.setImages(images);
        }

        Article savedArticle = articleRepository.save(article);

        if (articleCreateDTO.authors() != null && !articleCreateDTO.authors().isEmpty()) {
            List<ArticleAuthor> articleAuthors = articleCreateDTO.authors().stream()
                    .map(authorDTO -> {
                        Author author = authorRepository.findById(authorDTO.authorId())
                                .orElseThrow(() -> new ExceptionStatus("Author not found", "BAD_REQUEST"));

                        ArticleAuthor articleAuthor = new ArticleAuthor();
                        articleAuthor.setArticle(savedArticle);
                        articleAuthor.setAuthor(author);
                        articleAuthor.setContribution(authorDTO.contribution());
                        return articleAuthor;
                    })
                    .collect(Collectors.toList());

            articleAuthorRepository.saveAll(articleAuthors);
        }

        return ArticleDTO.mapFromEntity(
                articleRepository.findById(savedArticle.getId()).orElseThrow()
        );
    }

    @Transactional
    public ArticleDTO updateArticle(Long id, ArticleCreateDTO articleUpdateDTO) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Article not found", "NOT_FOUND"));

        // Vérification du titre s'il a changé
        if (!article.getTitle().equals(articleUpdateDTO.title()) && isTitleExist(articleUpdateDTO.title())) {
            throw new ExceptionStatus("Title already exists", "CONFLICT");
        }

        article.setTitle(articleUpdateDTO.title());
        article.setContent(articleUpdateDTO.content());
        article.setUpdatedAt(LocalDateTime.now());

        if (articleUpdateDTO.categoryId() != null) {
            Category category = categoryRepository.findById(articleUpdateDTO.categoryId())
                    .orElseThrow(() -> new ExceptionStatus("Category not found", "BAD_REQUEST"));
            article.setCategory(category);
        }

        if (articleUpdateDTO.imageIds() != null) {
            List<Image> images = imageRepository.findAllById(articleUpdateDTO.imageIds());
            if (images.size() != articleUpdateDTO.imageIds().size()) {
                throw new ExceptionStatus("Some images were not found", "BAD_REQUEST");
            }
            article.setImages(images);
        }

        if (articleUpdateDTO.authors() != null) {
            articleAuthorRepository.deleteByArticle(article);

            List<ArticleAuthor> articleAuthors = articleUpdateDTO.authors().stream()
                    .map(authorDTO -> {
                        Author author = authorRepository.findById(authorDTO.authorId())
                                .orElseThrow(() -> new ExceptionStatus("Author not found", "BAD_REQUEST"));

                        ArticleAuthor articleAuthor = new ArticleAuthor();
                        articleAuthor.setArticle(article);
                        articleAuthor.setAuthor(author);
                        articleAuthor.setContribution(authorDTO.contribution());
                        return articleAuthor;
                    })
                    .collect(Collectors.toList());

            articleAuthorRepository.saveAll(articleAuthors);
        }

        Article savedArticle = articleRepository.save(article);
        return ArticleDTO.mapFromEntity(savedArticle);
    }

    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Article not found with id: " + id, "NOT_FOUND"));
        articleRepository.delete(article);
    }

    public List<ArticleDTO> searchByTitle(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
        }
        List<Article> articles = articleRepository.findByTitle(query);
        if (articles.isEmpty()) {
            throw new ExceptionStatus("No articles found", "NOT_FOUND");
        }
        return articles.stream()
                .map(ArticleDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> searchByContent(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
        }
        List<Article> articles = articleRepository.findByContent(query);
        if (articles.isEmpty()) {
            throw new ExceptionStatus("No articles found", "NOT_FOUND");
        }
        return articles.stream()
                .map(ArticleDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> getArticlesCreatedAfter(LocalDateTime date) {
        if (date == null) {
            throw new ExceptionStatus("Date cannot be null", "BAD_REQUEST");
        }
        List<Article> articles = articleRepository.findByCreatedAtAfter(date);
        if (articles.isEmpty()) {
            throw new ExceptionStatus("No articles found", "NOT_FOUND");
        }
        return articles.stream()
                .map(ArticleDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public List<ArticleDTO> getLatestArticles() {
        Pageable pageable = PageRequest.of(0, 5);
        List<Article> articles = articleRepository.findTop5ByOrderByCreatedAtDesc(pageable);
        if (articles.isEmpty()) {
            throw new ExceptionStatus("No articles found", "NOT_FOUND");
        }
        return articles.stream()
                .map(ArticleDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    private boolean isTitleExist(String title) {
        return !articleRepository.findByTitle(title).isEmpty();
    }
}

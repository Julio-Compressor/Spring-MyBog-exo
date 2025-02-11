package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.Article;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Article}
 */
public record ArticleDTO(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        CategoryDTO category,
        List<ImageDTO> images,
        List<ArticleAuthorDTO> articleAuthors
) implements Serializable {
    public static ArticleDTO mapFromEntity(Article article) {
        return new ArticleDTO(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getCategory() != null ? CategoryDTO.mapFromEntity(article.getCategory()) : null,
                article.getImages().stream().map(ImageDTO::mapFromEntity).toList(),
                article.getArticleAuthors().stream().map(ArticleAuthorDTO::mapFromEntity).toList()
        );
    }
}

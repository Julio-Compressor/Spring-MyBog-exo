package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.ArticleAuthor;

import java.io.Serializable;

public record ArticleAuthorDTO(
        Long id,
        AuthorDTO author,
        String contribution
) implements Serializable {
    public static ArticleAuthorDTO mapFromEntity(ArticleAuthor articleAuthor) {
        return new ArticleAuthorDTO(
                articleAuthor.getId(),
                AuthorDTO.mapFromEntity(articleAuthor.getAuthor()),
                articleAuthor.getContribution()
        );
    }
}
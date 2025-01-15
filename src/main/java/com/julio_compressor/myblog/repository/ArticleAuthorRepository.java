package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Article;
import com.julio_compressor.myblog.model.ArticleAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleAuthorRepository extends JpaRepository<ArticleAuthor, Long> {
    void deleteByArticle(Article article);
}


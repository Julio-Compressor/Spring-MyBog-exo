package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}

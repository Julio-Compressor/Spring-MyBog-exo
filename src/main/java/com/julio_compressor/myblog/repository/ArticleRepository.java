package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTitle(String title);

    @Query("SELECT a FROM Article a WHERE LOWER(a.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    List<Article> findByContent(@Param("content") String content);

    List<Article> findByCreatedAtAfter(LocalDateTime date);

    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    List<Article> findTop5ByOrderByCreatedAtDesc(Pageable pageable);
}

package com.julio_compressor.myblog.dto;

import java.util.List;

public class CategoryDTO {
    private Long id;
    private String name;
    private List<ArticleDTO> articles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ArticleDTO> getArticles() {
        return articles;
    }
    public void setArticles(List<ArticleDTO> articles) {
        this.articles = articles;
    }
}

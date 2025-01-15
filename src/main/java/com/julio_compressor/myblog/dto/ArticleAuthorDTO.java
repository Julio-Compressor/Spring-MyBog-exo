package com.julio_compressor.myblog.dto;

import java.util.List;

public class ArticleAuthorDTO {
    private Long id;
    private List<Long> articleIds;
    private List<String> titles;
    private List<Long> authorIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(List<Long> articleIds) {
        this.articleIds = articleIds;
    }

    public List<Long> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<Long> authorIds) {
        this.authorIds = authorIds;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }
}

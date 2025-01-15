package com.julio_compressor.myblog.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "author")
    private List<ArticleAuthor> articleAuthors;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ArticleAuthor> getArticleAuthors() {
        return articleAuthors;
    }

    public void setArticleAuthors(List<ArticleAuthor> articleAuthors) {
        this.articleAuthors = articleAuthors;
    }
}

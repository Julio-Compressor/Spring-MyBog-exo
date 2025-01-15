package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.ArticleAuthorDTO;
import com.julio_compressor.myblog.dto.AuthorDTO;
import com.julio_compressor.myblog.exceptions.ExeptionStatus;
import com.julio_compressor.myblog.model.Author;
import com.julio_compressor.myblog.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<AuthorDTO> authorDTOs = authors.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(authorDTOs, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(author));
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody Author author) throws Exception {
        if (author.getFirstName() == null || author.getFirstName().trim().isEmpty() ||
                author.getLastName() == null || author.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (isAuthorExist(author)) {
            throw new ExeptionStatus("Author already exists", "CONFLICT");
        }

        Author savedAuthor = authorRepository.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAuthor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        if (!author.getId().equals(id) && isAuthorExist(authorDetails)) {
            throw new ExeptionStatus("Author already exists", "CONFLICT");
        }

        author.setFirstName(authorDetails.getFirstName());
        author.setLastName(authorDetails.getLastName());

        Author updatedAuthor = authorRepository.save(author);
        return ResponseEntity.ok(convertToDTO(updatedAuthor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        Author author = authorRepository.findById(id).orElse(null);
        if (author == null) {
            return ResponseEntity.notFound().build();
        }

        authorRepository.delete(author);
        return ResponseEntity.noContent().build();
    }

    private boolean isAuthorExist(Author author) {
        boolean b = authorRepository.findByFirstNameAndLastName(
                author.getFirstName(),
                author.getLastName()
        ).size() > 0;
        return b;
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(author.getId());
        authorDTO.setFirstName(author.getFirstName());
        authorDTO.setLastName(author.getLastName());

        if (author.getArticleAuthors() != null) {
            authorDTO.setArticleAuthorDTOList(author.getArticleAuthors().stream()
                    .map(articleAuthor -> {
                        ArticleAuthorDTO articleAuthorDTO = new ArticleAuthorDTO();
                        articleAuthorDTO.setId(articleAuthor.getId());
                        articleAuthorDTO.setArticleIds(List.of(articleAuthor.getArticle().getId()));
                        articleAuthorDTO.setAuthorIds(List.of(articleAuthor.getAuthor().getId()));
                        articleAuthorDTO.setTitles(List.of(articleAuthor.getArticle().getTitle()));
                        return articleAuthorDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return authorDTO;
    }
}

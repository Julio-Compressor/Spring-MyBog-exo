package com.julio_compressor.myblog.service;

import com.julio_compressor.myblog.dto.AuthorDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.model.Author;
import com.julio_compressor.myblog.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            throw new ExceptionStatus("No authors found", "NOT_FOUND");
        }
        return authors.stream()
                .map(AuthorDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Author not found", "NOT_FOUND"));
        return AuthorDTO.mapFromEntity(author);
    }

    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        if (authorDTO.firstName() == null || authorDTO.firstName().trim().isEmpty() ||
                authorDTO.lastName() == null || authorDTO.lastName().trim().isEmpty()) {
            throw new ExceptionStatus("First name and last name are required", "BAD_REQUEST");
        }

        Author author = new Author();
        author.setFirstName(authorDTO.firstName());
        author.setLastName(authorDTO.lastName());

        Author savedAuthor = authorRepository.save(author);
        return AuthorDTO.mapFromEntity(savedAuthor);
    }

    @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Author not found", "NOT_FOUND"));

        author.setFirstName(authorDTO.firstName());
        author.setLastName(authorDTO.lastName());

        Author updatedAuthor = authorRepository.save(author);
        return AuthorDTO.mapFromEntity(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ExceptionStatus("Author not found", "NOT_FOUND");
        }
        authorRepository.deleteById(id);
    }

    public List<AuthorDTO> searchAuthorsByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
        }
        List<Author> authors = authorRepository.findByFirstNameContainingOrLastNameContaining(query, query);
        if (authors.isEmpty()) {
            throw new ExceptionStatus("No authors found", "NOT_FOUND");
        }
        return authors.stream()
                .map(AuthorDTO::mapFromEntity)
                .collect(Collectors.toList());
    }
}
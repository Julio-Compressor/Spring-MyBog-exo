package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.Author;

import java.io.Serializable;
/**
 * DTO for {@link Author}
 */
public record AuthorDTO(
        Long id,
        String firstName,
        String lastName
) implements Serializable {
    public static AuthorDTO mapFromEntity(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getFirstName(),
                author.getLastName()
        );
    }
}

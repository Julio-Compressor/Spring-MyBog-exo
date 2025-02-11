package com.julio_compressor.myblog.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;

public record ArticleAuthorCreateDTO(
        @NotNull(message = "L'ID de l'auteur ne doit pas être nul")
        @Positive(message = "L'ID de l'auteur doit être un nombre positif")
        Long authorId,

        @NotBlank(message = "La contribution de l'auteur ne doit pas être vide")
        String contribution
) implements Serializable {}

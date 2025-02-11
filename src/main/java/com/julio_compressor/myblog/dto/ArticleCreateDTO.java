package com.julio_compressor.myblog.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.List;

public record ArticleCreateDTO(
        @NotBlank(message = "Le titre ne doit pas être vide")
        @Size(min = 2, max = 50, message = "Le titre doit contenir entre 2 et 50 caractères")
        String title,

        @NotBlank(message = "Le contenu ne doit pas être vide")
        @Size(min = 10, message = "Le contenu doit contenir au moins 10 caractères")
        String content,

        @NotNull(message = "L'ID de la catégorie ne doit pas être nul")
        @Positive(message = "L'ID de la catégorie doit être un nombre positif")
        Long categoryId,

        List<Long> imageIds,

        @NotEmpty(message = "La liste des auteurs ne doit pas être vide")
        List<ArticleAuthorCreateDTO> authors
) implements Serializable {}

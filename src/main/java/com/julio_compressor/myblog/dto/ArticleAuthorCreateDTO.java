package com.julio_compressor.myblog.dto;

import java.io.Serializable;

public record ArticleAuthorCreateDTO(
        Long authorId,
        String contribution
) implements Serializable {}

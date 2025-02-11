package com.julio_compressor.myblog.dto;

import java.io.Serializable;
import java.util.List;

public record ArticleCreateDTO(
        String title,
        String content,
        Long categoryId,
        List<Long> imageIds,
        List<ArticleAuthorCreateDTO> authors
) implements Serializable {}

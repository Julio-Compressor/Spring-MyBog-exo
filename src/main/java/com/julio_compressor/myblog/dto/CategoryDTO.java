package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.Category;

import java.io.Serializable;

public record CategoryDTO(
        Long id,
        String name
) implements Serializable {
    public static CategoryDTO mapFromEntity(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName()
        );
    }
}



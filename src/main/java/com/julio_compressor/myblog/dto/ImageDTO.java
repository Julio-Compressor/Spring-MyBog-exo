package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.Image;

import java.io.Serializable;


public record ImageDTO(
        Long id,
        String url
) implements Serializable {
    public static ImageDTO mapFromEntity(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getUrl()
        );
    }
}

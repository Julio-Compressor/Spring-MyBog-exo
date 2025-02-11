package com.julio_compressor.myblog.dto;

import com.julio_compressor.myblog.model.Image;
import org.hibernate.validator.constraints.*;

import java.io.Serializable;


public record ImageDTO(
        Long id,
        @URL(message = "L'URL de l'image doit être valide")
        String url
) implements Serializable {
    public static ImageDTO mapFromEntity(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getUrl()
        );
    }
}

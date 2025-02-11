package com.julio_compressor.myblog.service;

import com.julio_compressor.myblog.dto.ImageDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.model.Image;
import com.julio_compressor.myblog.repository.ImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageDTO> getAllImages() {
        List<Image> images = imageRepository.findAll();
        if (images.isEmpty()) {
            throw new ExceptionStatus("No images found", "NOT_FOUND");
        }
        return images.stream()
                .map(ImageDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public ImageDTO getImageById(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Image not found", "NOT_FOUND"));
        return ImageDTO.mapFromEntity(image);
    }

    @Transactional
    public ImageDTO createImage(ImageDTO imageDTO) {
        if (imageDTO.url() == null || imageDTO.url().trim().isEmpty()) {
            throw new ExceptionStatus("Image URL is required", "BAD_REQUEST");
        }

        // Vérifier si l'URL existe déjà
        if (imageRepository.findByUrl(imageDTO.url()).isPresent()) {
            throw new ExceptionStatus("Image URL already exists", "CONFLICT");
        }

        Image image = new Image();
        image.setUrl(imageDTO.url());

        Image savedImage = imageRepository.save(image);
        return ImageDTO.mapFromEntity(savedImage);
    }

    @Transactional
    public ImageDTO updateImage(Long id, ImageDTO imageDTO) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Image not found", "NOT_FOUND"));

        // Vérifier si la nouvelle URL existe déjà et n'appartient pas à l'image actuelle
        imageRepository.findByUrl(imageDTO.url())
                .ifPresent(existingImage -> {
                    if (!existingImage.getId().equals(id)) {
                        throw new ExceptionStatus("Image URL already exists", "CONFLICT");
                    }
                });

        image.setUrl(imageDTO.url());
        Image updatedImage = imageRepository.save(image);
        return ImageDTO.mapFromEntity(updatedImage);
    }

    @Transactional
    public void deleteImage(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Image not found", "NOT_FOUND"));

        // Vérifier si l'image est utilisée par des articles
        if (!image.getArticles().isEmpty()) {
            throw new ExceptionStatus("Cannot delete image: it is still being used by articles", "CONFLICT");
        }

        imageRepository.delete(image);
    }

    public List<ImageDTO> searchImages(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
        }
        List<Image> images = imageRepository.findByUrlContaining(query);
        if (images.isEmpty()) {
            throw new ExceptionStatus("No images found", "NOT_FOUND");
        }
        return images.stream()
                .map(ImageDTO::mapFromEntity)
                .collect(Collectors.toList());
    }
}
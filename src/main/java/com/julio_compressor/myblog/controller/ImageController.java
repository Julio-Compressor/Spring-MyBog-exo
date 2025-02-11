package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.dto.ImageDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        try {
            List<ImageDTO> images = imageService.getAllImages();
            return ResponseEntity.ok(images);
        } catch (ExceptionStatus e) {
            throw new ExceptionStatus(e.getMessage(), "NOT_FOUND");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @PostMapping
    public ResponseEntity<ImageDTO> createImage(@RequestBody ImageDTO imageDTO) {
        ImageDTO createdImage = imageService.createImage(imageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdImage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImageDTO> updateImage(
            @PathVariable Long id,
            @RequestBody ImageDTO imageDTO
    ) {
        return ResponseEntity.ok(imageService.updateImage(id, imageDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ImageDTO>> searchImages(@RequestParam String query) {
        return ResponseEntity.ok(imageService.searchImages(query));
    }
}
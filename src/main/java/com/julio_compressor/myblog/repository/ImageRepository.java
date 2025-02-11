package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUrlContaining(String query);

    Optional<Image> findByUrl(String url);
}

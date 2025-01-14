package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

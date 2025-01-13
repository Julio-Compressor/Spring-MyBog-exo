package com.julio_compressor.myblog.repository;

import com.julio_compressor.myblog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

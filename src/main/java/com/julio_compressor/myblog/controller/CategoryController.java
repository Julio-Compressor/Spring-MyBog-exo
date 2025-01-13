package com.julio_compressor.myblog.controller;

import com.julio_compressor.myblog.model.Category;
import com.julio_compressor.myblog.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category categorySaved = categoryRepository.save(category);
        return new ResponseEntity<>(categorySaved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        categoryRepository.delete(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        category.setName(categoryDetails.getName());
        categoryRepository.save(category);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}

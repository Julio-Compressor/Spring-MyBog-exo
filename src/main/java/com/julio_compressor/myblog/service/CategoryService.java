package com.julio_compressor.myblog.service;

import com.julio_compressor.myblog.dto.CategoryDTO;
import com.julio_compressor.myblog.exceptions.ExceptionStatus;
import com.julio_compressor.myblog.model.Category;
import com.julio_compressor.myblog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new ExceptionStatus("No categories found", "NOT_FOUND");
        }
        return categories.stream()
                .map(CategoryDTO::mapFromEntity)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Category not found", "NOT_FOUND"));
        return CategoryDTO.mapFromEntity(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.name() == null || categoryDTO.name().trim().isEmpty()) {
            throw new ExceptionStatus("Category name is required", "BAD_REQUEST");
        }

        // Vérifier si le nom de catégorie existe déjà
        if (categoryRepository.findByName(categoryDTO.name()).isPresent()) {
            throw new ExceptionStatus("Category name already exists", "CONFLICT");
        }

        Category category = new Category();
        category.setName(categoryDTO.name());

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.mapFromEntity(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Category not found", "NOT_FOUND"));

        // Vérifier si le nouveau nom existe déjà et n'appartient pas à la catégorie actuelle
        categoryRepository.findByName(categoryDTO.name())
                .ifPresent(existingCategory -> {
                    if (!existingCategory.getId().equals(id)) {
                        throw new ExceptionStatus("Category name already exists", "CONFLICT");
                    }
                });

        category.setName(categoryDTO.name());
        Category updatedCategory = categoryRepository.save(category);
        return CategoryDTO.mapFromEntity(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionStatus("Category not found", "NOT_FOUND"));

        // Vérifier si la catégorie est utilisée par des articles
        if (!category.getArticles().isEmpty()) {
            throw new ExceptionStatus("Cannot delete category: it is still being used by articles", "CONFLICT");
        }

        categoryRepository.delete(category);
    }

    public List<CategoryDTO> searchCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ExceptionStatus("Search query cannot be empty", "BAD_REQUEST");
        }
        List<Category> categories = categoryRepository.findByNameContaining(query);
        if (categories.isEmpty()) {
            throw new ExceptionStatus("No categories found", "NOT_FOUND");
        }
        return categories.stream()
                .map(CategoryDTO::mapFromEntity)
                .collect(Collectors.toList());
    }
}
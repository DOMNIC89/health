package com.example.health.service;

import com.example.health.co.CategoryResponseCO;
import com.example.health.exception.CategoryNotFoundException;
import com.example.health.model.Category;
import com.example.health.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponseCO> findAllCategories() {
        List<Category> categories = StreamSupport.stream(categoryRepository.findAll().spliterator(), false).collect(Collectors.toList());
        List<CategoryResponseCO> categoryResponseCOS = categories.stream().map(CategoryResponseCO::toCategoryResponseCO).collect(Collectors.toList());
        if (categoryResponseCOS.isEmpty()) {
            throw new CategoryNotFoundException("Categories not found!");
        }
        return categoryResponseCOS;
    }
}

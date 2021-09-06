package com.example.health.controller;

import com.example.health.co.CategoriesCO;
import com.example.health.co.CategoryResponseCO;
import com.example.health.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<CategoriesCO> listAllCategories() {
        List<CategoryResponseCO> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(new CategoriesCO((long) categories.size(), categories));
    }
}

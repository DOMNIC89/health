package com.example.health.config;

import com.example.health.model.Category;
import com.example.health.repository.CategoryRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;

@Component
public class HealthConfig implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    public HealthConfig(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File categoryJson = ResourceUtils.getFile("classpath:category.json");
        List<Category> categories = mapper.readValue(categoryJson, new TypeReference<>() {});
        categoryRepository.saveAll(categories);
    }
}

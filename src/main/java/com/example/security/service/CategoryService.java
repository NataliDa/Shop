package com.example.security.service;

import com.example.security.models.Category;
import com.example.security.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

        createCategories();
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(int id) {
        return categoryRepository.findById(id).orElseThrow();
    }

    @Transactional
    void createCategories() {
        List<Category> categories = new ArrayList<>();
        if (categoryRepository.findByName("Мебель") == null) {
            categories.add(new Category("Мебель"));
        }
        if (categoryRepository.findByName("Бытовая техника") == null) {
            categories.add(new Category("Бытовая техника"));
        }
        if (categoryRepository.findByName("Одежда") == null) {
            categories.add(new Category("Одежда"));
        }
        if (categories.size() > 0) {
            categoryRepository.saveAll(categories);
        }
    }
}

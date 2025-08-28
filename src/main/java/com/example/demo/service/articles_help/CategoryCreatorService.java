package com.example.demo.service.articles_help;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.entity.Category;
import com.example.demo.interfaces.articles_help.CategoryCreatorInterface;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryCreatorService implements CategoryCreatorInterface {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(CategoryCreateRequest req) {
        String name = req.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists.");
        }
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }
}

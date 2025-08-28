package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.entity.Category;

public interface CategoryCreatorInterface {
    Category create(CategoryCreateRequest req);
}

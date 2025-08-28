package com.example.demo.controller;

import com.example.demo.dto.CategoryCreateRequest;
import com.example.demo.dto.TagCreateRequest;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import com.example.demo.interfaces.articles_help.CategoryCreatorInterface;
import com.example.demo.interfaces.articles_help.TagCreatorInterface;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taxonomy")
@RequiredArgsConstructor
public class TaxonomyController {

    private final TagCreatorInterface tagCreator;
    private final CategoryCreatorInterface categoryCreator;

    @Operation(summary = "Create a tag", description = "Creates a new tag by unique name.")
    @PostMapping("/tags")
    public Tag createTag(@RequestBody TagCreateRequest req) {
        return tagCreator.create(req);
    }

    @Operation(summary = "Create a category", description = "Creates a new category by unique name.")
    @PostMapping("/categories")
    public Category createCategory(@RequestBody CategoryCreateRequest req) {
        return categoryCreator.create(req);
    }
}

package com.example.demo.controller;

import com.example.demo.dto.ArticleBlogResponse;
import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.interfaces.articles_help.ArticleInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleInterface articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> create(@RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(@PathVariable Long id,
                                                  @RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/blog")
    public ResponseEntity<List<ArticleBlogResponse>> blog(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(articleService.getAllArticlesAsBlog(page));
    }
}

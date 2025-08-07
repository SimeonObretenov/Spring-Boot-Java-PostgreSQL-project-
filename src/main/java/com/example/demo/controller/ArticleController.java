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

    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleCreateRequest request) {
        ArticleResponse updated = articleService.updateArticle(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/blog")
    @Operation(summary = "Get paginated blog articles", description = "Returns blog articles, 10 per page.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated blog articles")
    public ResponseEntity<List<ArticleBlogResponse>> getAllArticlesAsBlog(
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(articleService.getAllArticlesAsBlog(page));
    }


}

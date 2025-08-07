package com.example.demo.Controller;

import com.example.demo.Dto.ArticleBlogResponse;
import com.example.demo.Dto.ArticleCreateRequest;
import com.example.demo.Dto.ArticleResponse;
import com.example.demo.Entity.Article;
import com.example.demo.Interfaces.ArticlesHelp.ArticleInterface;
import com.example.demo.Service.ArticlesHelp.ArticleService;
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
    @Operation(summary = "Get all blog articles", description = "Returns a list of all articles with title, author, tags, and category.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved blog articles")
    public ResponseEntity<List<ArticleBlogResponse>> getAllArticlesAsBlog() {
        return ResponseEntity.ok(articleService.getAllArticlesAsBlog());
    }

}

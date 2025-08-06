package com.example.demo.Controller;

import com.example.demo.Dto.ArticleCreateRequest;
import com.example.demo.Dto.ArticleResponse;
import com.example.demo.Entity.Article;
import com.example.demo.Interfaces.ArticlesHelp.ArticleInterface;
import com.example.demo.Service.ArticlesHelp.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleInterface articleService;

    @PostMapping("/articles")
    public ResponseEntity<ArticleResponse> createArticle(@RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

}

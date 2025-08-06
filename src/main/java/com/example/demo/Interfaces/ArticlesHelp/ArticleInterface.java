package com.example.demo.Interfaces.ArticlesHelp;

import com.example.demo.Dto.ArticleCreateRequest;
import com.example.demo.Dto.ArticleResponse;
import com.example.demo.Entity.Article;
import java.util.Set;

public interface ArticleInterface {
    ArticleResponse createArticle(ArticleCreateRequest request);
    void deleteArticle(Long articleId);
}

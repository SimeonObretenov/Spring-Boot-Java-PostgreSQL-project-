package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.ArticleBlogResponse;
import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;

import java.util.List;

public interface ArticleInterface {
    ArticleResponse createArticle(ArticleCreateRequest request);
    void deleteArticle(Long articleId);
    ArticleResponse updateArticle(Long articleId, ArticleCreateRequest request);
    List<ArticleBlogResponse> getAllArticlesAsBlog(int page);
}

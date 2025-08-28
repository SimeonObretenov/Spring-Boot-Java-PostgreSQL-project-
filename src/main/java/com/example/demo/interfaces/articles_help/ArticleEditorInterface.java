package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;

public interface ArticleEditorInterface {
    ArticleResponse update(Long articleId, ArticleCreateRequest request);
}

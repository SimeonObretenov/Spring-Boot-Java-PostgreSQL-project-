package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;

public interface ArticleCreatorInterface {
    ArticleResponse create(ArticleCreateRequest request);
}

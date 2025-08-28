package com.example.demo.interfaces.articles_help;

import com.example.demo.dto.ArticleBlogResponse;
import com.example.demo.dto.ArticleResponse;

import java.util.List;

public interface ArticleReaderInterface  {
    List<ArticleBlogResponse> listBlog(int page);
    ArticleResponse getOne(Long articleId);
}

package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Set;

@Data
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private String title;
    private String content;
    private String categoryName;
    private String authorName;
    private Set<String> tagNames;
}

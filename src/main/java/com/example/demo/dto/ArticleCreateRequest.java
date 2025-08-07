package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class ArticleCreateRequest {
    private String title;
    private String content;
    private Long categoryId;
    private Set<Long> tagIds;
}

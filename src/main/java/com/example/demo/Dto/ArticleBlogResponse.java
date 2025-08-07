package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleBlogResponse {
    private String title;
    private String author;
    private List<String> tags;
    private List<String> categories;
}
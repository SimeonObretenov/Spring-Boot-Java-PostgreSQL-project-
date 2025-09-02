package com.example.demo;

import com.example.demo.dto.ArticleBlogResponse;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.entity.Person;
import com.example.demo.entity.Tag;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.articles_help.ArticleReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleReaderServiceTest {

    @Mock ArticleRepository articleRepo;

    @InjectMocks
    ArticleReaderService service;

    private Article buildArticle(Long id, String title, String categoryName, String authorName, String... tags) {
        var category = Category.builder().id(1L).name(categoryName).build();
        var author = Person.builder().id(1L).name(authorName).build();
        var tagSet = Set.of(tags).stream()
                .map(name -> Tag.builder().id((long) name.hashCode()).name(name).build())
                .collect(java.util.stream.Collectors.toSet());

        return Article.builder()
                .id(id)
                .title(title)
                .content("Some content")
                .category(category)
                .author(author)
                .tags(tagSet)
                .build();
    }

    @Test
    void listBlogShouldReturnMappedResponses() {
        var article1 = buildArticle(1L, "Spring Boot Intro", "Tech", "Moni", "Spring", "Java");
        var article2 = buildArticle(2L, "Hibernate Deep Dive", "Databases", "Alex", "Hibernate");

        when(articleRepo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(article1, article2)));

        List<ArticleBlogResponse> result = service.listBlog(0);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Spring Boot Intro");
        assertThat(result.get(0).getAuthor()).isEqualTo("Moni");
        assertThat(result.get(0).getTags()).containsExactlyInAnyOrder("Spring", "Java");
        assertThat(result.get(0).getCategories()).containsExactly("Tech");

        assertThat(result.get(1).getTitle()).isEqualTo("Hibernate Deep Dive");
        assertThat(result.get(1).getAuthor()).isEqualTo("Alex");
        assertThat(result.get(1).getTags()).containsExactly("Hibernate");
        assertThat(result.get(1).getCategories()).containsExactly("Databases");
    }

    @Test
    void getOneShouldReturnMappedResponse() {
        var article = buildArticle(1L, "Test Article", "Tech", "Moni", "Spring", "Java");

        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));

        ArticleResponse result = service.getOne(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Article");
        assertThat(result.getContent()).isEqualTo("Some content");
        assertThat(result.getCategoryName()).isEqualTo("Tech");
        assertThat(result.getAuthorName()).isEqualTo("Moni");
        assertThat(result.getTagNames()).containsExactlyInAnyOrder("Spring", "Java");
    }

    @Test
    void getOneShouldThrowWhenNotFound() {
        when(articleRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOne(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found");
    }
}

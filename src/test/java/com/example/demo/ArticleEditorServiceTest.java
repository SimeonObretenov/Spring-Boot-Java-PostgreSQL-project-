package com.example.demo;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.entity.Article;
import com.example.demo.entity.Category;
import com.example.demo.entity.Person;
import com.example.demo.entity.Tag;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.service.articles_help.ArticleEditorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleEditorServiceTest {

    @Mock ArticleRepository articleRepo;
    @Mock CategoryRepository categoryRepo;
    @Mock TagRepository tagRepo;

    @InjectMocks ArticleEditorService service;

    @Test
    void updateShouldModifyArticleCorrectly() {
        var author = Person.builder().id(10L).name("Moni").build();
        var article = Article.builder()
                .id(1L)
                .title("Old Title")
                .content("Old Content")
                .author(author)
                .build();

        var category = Category.builder().id(1L).name("Tech").build();
        var springTag = Tag.builder().id(1L).name("Spring").build();
        var javaTag   = Tag.builder().id(2L).name("Java").build();

        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(tagRepo.findByIdIn(Set.of(1L, 2L))).thenReturn(Set.of(springTag, javaTag));
        when(articleRepo.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = new ArticleCreateRequest("New Title", "New Content", 1L, Set.of(1L, 2L));

        ArticleResponse result = service.update(1L, req);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getContent()).isEqualTo("New Content");
        assertThat(result.getCategoryName()).isEqualTo("Tech");
        assertThat(result.getAuthorName()).isEqualTo("Moni");
        assertThat(result.getTagNames()).containsExactlyInAnyOrder("Spring", "Java");

        verify(articleRepo).save(article);
    }

    @Test
    void updateThrowsWhenArticleNotFound() {
        when(articleRepo.findById(99L)).thenReturn(Optional.empty());

        var req = new ArticleCreateRequest("X", "Y", 1L, Set.of());

        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found");
    }

    @Test
    void updateThrowsWhenCategoryNotFound() {
        var article = Article.builder().id(1L).build();

        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));
        when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

        var req = new ArticleCreateRequest("X", "Y", 1L, Set.of());

        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Category not found");
    }

    @Test
    void updateThrowsWhenTagsMissing() {
        var article = Article.builder().id(1L).build();
        var category = Category.builder().id(1L).name("Tech").build();
        var springTag = Tag.builder().id(1L).name("Spring").build();

        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(tagRepo.findByIdIn(Set.of(1L, 2L))).thenReturn(Set.of(springTag));

        var req = new ArticleCreateRequest("X", "Y", 1L, Set.of(1L, 2L));

        assertThatThrownBy(() -> service.update(1L, req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Tag(s) not found");
    }
}

package com.example.demo.Service.ArticlesHelp;

import com.example.demo.Dto.ArticleCreateRequest;
import com.example.demo.Dto.ArticleResponse;
import com.example.demo.Entity.Article;
import com.example.demo.Entity.Category;
import com.example.demo.Entity.Person;
import com.example.demo.Entity.Tag;
import com.example.demo.Interfaces.ArticlesHelp.ArticleInterface;
import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleInterface {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Override
    public ArticleResponse createArticle(ArticleCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Person author = (Person) authentication.getPrincipal();
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Category category = categoryRepository.findCategoryById(request.getCategoryId());
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        Set<Tag> tags = (request.getTagIds() != null && !request.getTagIds().isEmpty())
                ? tagRepository.findByIdIn(request.getTagIds())
                : new HashSet<>();

        Article article = new Article(request.getTitle(), request.getContent(), category, author);
        article.setTags(tags);

        Article saved = articleRepository.save(article);

        return new ArticleResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getCategory().getName(),
                saved.getAuthor().getName(),
                saved.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
        );
    }
}

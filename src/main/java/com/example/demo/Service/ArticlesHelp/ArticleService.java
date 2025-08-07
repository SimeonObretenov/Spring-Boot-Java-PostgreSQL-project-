package com.example.demo.Service.ArticlesHelp;

import com.example.demo.Dto.ArticleBlogResponse;
import com.example.demo.Dto.ArticleCreateRequest;
import com.example.demo.Dto.ArticleResponse;
import com.example.demo.Entity.*;
import com.example.demo.Interfaces.ArticlesHelp.ArticleInterface;
import com.example.demo.Repository.ArticleRepository;
import com.example.demo.Repository.CategoryRepository;
import com.example.demo.Repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
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

    @Override
    public void deleteArticle(Long articleId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = (Person) auth.getPrincipal();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAuthor = article.getAuthor().getId().equals(currentUser.getId());

        if (isAdmin || isAuthor) {
            articleRepository.delete(article);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this article");
        }
    }

    @Override
    public ArticleResponse updateArticle(Long articleId, ArticleCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = (Person) auth.getPrincipal();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isAuthor = article.getAuthor().getId().equals(currentUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this article");
        }

        Category category = categoryRepository.findCategoryById(request.getCategoryId());
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }
        article.setCategory(category);

        Set<Tag> tags = (request.getTagIds() != null && !request.getTagIds().isEmpty())
                ? tagRepository.findByIdIn(request.getTagIds())
                : new HashSet<>();
        article.setTags(tags);

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

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

    @Override
    public List<ArticleBlogResponse> getAllArticlesAsBlog(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<Article> articles = articleRepository.findAll(pageable).getContent();

        return articles.stream().map(article -> {
            String authorName = article.getAuthor().getName();

            List<String> tags = article.getTags().stream()
                    .map(Tag::getName)
                    .toList();

            List<String> categories = List.of(article.getCategory().getName());

            return new ArticleBlogResponse(
                    article.getTitle(),
                    authorName,
                    tags,
                    categories
            );
        }).toList();
    }
}

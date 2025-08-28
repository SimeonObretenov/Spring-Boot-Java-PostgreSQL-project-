package com.example.demo.service.articles_help;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.entity.Tag;
import com.example.demo.interfaces.articles_help.ArticleEditorInterface;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleEditorService implements ArticleEditorInterface {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;
    private final TagRepository tagRepo;

    @Override
    @PreAuthorize("hasPermission(T(java.lang.Long).valueOf(#articleId), 'com.example.demo.entity.Article', 'WRITE')")
    public ArticleResponse update(@P("articleId") Long articleId, ArticleCreateRequest request) {
        var article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

        var category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        article.setCategory(category);

        Set<Long> requestedIds = request.getTagIds();
        Set<Tag> tags = (requestedIds != null && !requestedIds.isEmpty())
                ? tagRepo.findByIdIn(requestedIds)
                : Collections.emptySet();

        if (requestedIds != null && !requestedIds.isEmpty() && tags.size() != requestedIds.size()) {
            var foundIds = tags.stream().map(Tag::getId).collect(Collectors.toSet());
            var missing = new HashSet<>(requestedIds);
            missing.removeAll(foundIds);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag(s) not found: " + missing);
        }

        article.setTags(tags);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());

        var saved = articleRepo.save(article);

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
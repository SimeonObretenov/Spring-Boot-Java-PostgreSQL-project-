package com.example.demo.service.articles_help;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.entity.Article;
import com.example.demo.entity.Person;
import com.example.demo.entity.Tag;
import com.example.demo.events.ArticleCreatedEvent;
import com.example.demo.interfaces.articles_help.ArticleCreatorInterface;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleCreatorService implements ArticleCreatorInterface {

    private final ArticleRepository articleRepo;
    private final CategoryRepository categoryRepo;
    private final TagRepository tagRepo;
    private final ApplicationEventPublisher events;
    private final PersonRepository personRepo;

    @Override
    public ArticleResponse create(ArticleCreateRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String username = (String) auth.getPrincipal();
        Person author = personRepo.findByUsername(username);
        if (author == null || !author.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or inactive");
        }

        var category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Set<Tag> tags = (request.getTagIds() != null && !request.getTagIds().isEmpty())
                ? tagRepo.findByIdIn(request.getTagIds())
                : Collections.emptySet();

        var article = new Article(request.getTitle(), request.getContent(), category, author);
        article.setTags(tags);
        var saved = articleRepo.save(article);

        System.out.println("Publishing ACL event with username = " + author.getUsername());
        events.publishEvent(new ArticleCreatedEvent(saved.getId(), author.getUsername()));

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

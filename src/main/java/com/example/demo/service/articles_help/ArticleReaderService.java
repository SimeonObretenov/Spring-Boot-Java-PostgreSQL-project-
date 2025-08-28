package com.example.demo.service.articles_help;

import com.example.demo.dto.ArticleBlogResponse;
import com.example.demo.dto.ArticleResponse;
import com.example.demo.entity.Tag;
import com.example.demo.interfaces.articles_help.ArticleReaderInterface;
import com.example.demo.repository.ArticleRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleReaderService implements ArticleReaderInterface {
    private final ArticleRepository articleRepo;

    @Override
    public List<ArticleBlogResponse> listBlog(int page) {
        var pageable = PageRequest.of(page, 10);
        return articleRepo.findAll(pageable).getContent().stream()
                .map(a -> new ArticleBlogResponse(
                        a.getTitle(),
                        a.getAuthor().getName(),
                        a.getTags().stream().map(Tag::getName).toList(),
                        List.of(a.getCategory().getName())
                )).toList();
    }

    @Override
    public ArticleResponse getOne(Long articleId) {
        var a = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));
        return new ArticleResponse(
                a.getId(), a.getTitle(), a.getContent(),
                a.getCategory().getName(), a.getAuthor().getName(),
                a.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
        );
    }
}


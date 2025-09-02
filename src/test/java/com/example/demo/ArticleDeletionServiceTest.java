package com.example.demo;

import com.example.demo.entity.Article;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.service.articles_help.ArticleDeletionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleDeletionServiceTest {

    @Mock ArticleRepository articleRepo;
    @Mock MutableAclService aclService;

    @InjectMocks
    ArticleDeletionService service;

    @Test
    void deleteShouldRemoveArticleAndAcl() {
        var article = Article.builder().id(1L).title("A").content("C").build();
        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));

        service.delete(1L);

        verify(articleRepo).delete(article);
        verify(aclService).deleteAcl(new ObjectIdentityImpl(Article.class, 1L), true);
    }

    @Test
    void deleteShouldThrowWhenArticleNotFound() {
        when(articleRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Article not found");

        verify(articleRepo, never()).delete(any());
        verifyNoInteractions(aclService);
    }

    @Test
    void deleteShouldIgnoreMissingAcl() {
        var article = Article.builder().id(1L).title("A").content("C").build();
        when(articleRepo.findById(1L)).thenReturn(Optional.of(article));
        doThrow(new NotFoundException("no acl")).when(aclService)
                .deleteAcl(new ObjectIdentityImpl(Article.class, 1L), true);

        service.delete(1L);

        verify(articleRepo).delete(article);
        verify(aclService).deleteAcl(new ObjectIdentityImpl(Article.class, 1L), true);
    }
}

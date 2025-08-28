    package com.example.demo.service.articles_help;

    import com.example.demo.entity.Article;
    import com.example.demo.interfaces.articles_help.ArticleDeletionInterface;
    import com.example.demo.repository.ArticleRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.parameters.P;
    import org.springframework.security.acls.domain.ObjectIdentityImpl;
    import org.springframework.security.acls.model.MutableAclService;
    import org.springframework.security.acls.model.NotFoundException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.server.ResponseStatusException;

    @Service
    @RequiredArgsConstructor
    @Transactional
    public class ArticleDeletionService implements ArticleDeletionInterface {

        private final ArticleRepository articleRepo;
        private final MutableAclService aclService;

        @PreAuthorize("hasPermission(T(java.lang.Long).valueOf(#articleId), 'com.example.demo.entity.Article', 'DELETE')")
        @Override
        public void delete(@P("articleId") Long articleId) {
            var article = articleRepo.findById(articleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Article not found"));

            articleRepo.delete(article);

            try {
                var oi = new ObjectIdentityImpl(Article.class, articleId);
                aclService.deleteAcl(oi, true);
            } catch (NotFoundException ignored) { }
        }
    }
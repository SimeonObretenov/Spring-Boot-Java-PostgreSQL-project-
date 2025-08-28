package com.example.demo.service.articles_help;

import com.example.demo.entity.Article;
import com.example.demo.events.ArticleCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleAclSeeder {

    private final MutableAclService aclService;

    @EventListener
    @Transactional
    public void handleArticleCreated(ArticleCreatedEvent event) {
        ObjectIdentity oi = new ObjectIdentityImpl(Article.class, event.articleId());

        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(oi);
        } catch (NotFoundException ex) {
            acl = aclService.createAcl(oi);
        }

        Sid owner = new PrincipalSid(event.ownerUsername());
        acl.setOwner(owner);

        // Grant creator permissions: READ + WRITE + DELETE
        acl.insertAce(acl.getEntries().size(), BasePermission.READ, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, owner, true);
        acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, owner, true);

        aclService.updateAcl(acl);
    }
}

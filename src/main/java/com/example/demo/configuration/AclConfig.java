package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.sql.DataSource;

@Configuration
@EnableCaching
public class AclConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager mgr = new CaffeineCacheManager("aclCache");
        mgr.setCaffeine(Caffeine.newBuilder().maximumSize(10_000));
        return mgr;
    }

    @Bean
    public Cache aclSpringCache(CacheManager cacheManager) {
        return cacheManager.getCache("aclCache");
    }

    @Bean
    public AuditLogger auditLogger() {
        return new ConsoleAuditLogger();
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy(AuditLogger auditLogger) {
        return new DefaultPermissionGrantingStrategy(auditLogger);
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public AclCache aclCache(Cache aclSpringCache,
                             PermissionGrantingStrategy pgs,
                             AclAuthorizationStrategy aas) {
        return new SpringCacheBasedAclCache(aclSpringCache, pgs, aas);
    }

    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource,
                                         AclCache aclCache,
                                         AclAuthorizationStrategy aas,
                                         AuditLogger auditLogger) {
        return new BasicLookupStrategy(dataSource, aclCache, aas, auditLogger);
    }

    @Bean
    public JdbcMutableAclService aclService(
            DataSource dataSource,
            LookupStrategy lookupStrategy,
            AclCache aclCache,
            Environment env) {

        JdbcMutableAclService svc = new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);

        if (env.acceptsProfiles("test")) {
            svc.setClassIdentityQuery("select max(id) from acl_class");
            svc.setSidIdentityQuery("select max(id) from acl_sid");
        } else {
            svc.setClassIdentityQuery("select currval(pg_get_serial_sequence('acl_class','id'))");
            svc.setSidIdentityQuery("select currval(pg_get_serial_sequence('acl_sid','id'))");
        }

        return svc;
    }


}

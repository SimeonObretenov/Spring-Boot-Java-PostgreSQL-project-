package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ArticleCreateRequest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleEditTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String loginAndGetToken() {
        String loginUrl = "http://localhost:" + port + "/login";
        LoginRequest req = new LoginRequest("moni", "moni");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> resp =
                restTemplate.postForEntity(loginUrl, new HttpEntity<>(req, headers), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = resp.getBody();
        return token.replace("{\"token\":\"", "").replace("\"}", "");
    }

    @Test
    @DataSet({
            "datasets/person.yml",
            "datasets/article/category.yml",
            "datasets/article/article.yml",
            "datasets/article/tag.yml",
            "datasets/article/article_tag.yml",
            "datasets/acl/acl_sid.yml",
            "datasets/acl/acl_class.yml",
            "datasets/acl/acl_object_identity.yml",
            "datasets/acl/acl_entry.yml"
    })
    void userCanEditArticle() {
        String token = loginAndGetToken();

        String url = "http://localhost:" + port + "/api/articles/1";
        ArticleCreateRequest editReq = new ArticleCreateRequest(
                "Updated Title",
                "Updated Content",
                1L,
                Set.of(1L, 2L)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(editReq, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Updated Title");
        assertThat(response.getBody()).contains("Updated Content");
        assertThat(response.getBody()).contains("Spring");
        assertThat(response.getBody()).contains("Java");
    }

    @Test
    @DataSet({
            "datasets/person.yml",
            "datasets/article/category.yml",
            "datasets/article/article.yml",
            "datasets/article/tag.yml",
            "datasets/article/article_tag.yml",
            "datasets/acl/acl_sid.yml",
            "datasets/acl/acl_class.yml",
            "datasets/acl/acl_object_identity.yml",
            "datasets/acl/acl_entry.yml"
    })
    void editingNonExistingArticleReturns404() {
        String token = loginAndGetToken();

        String url = "http://localhost:" + port + "/api/articles/999";
        ArticleCreateRequest editReq = new ArticleCreateRequest(
                "Ghost Title",
                "Ghost Content",
                1L,
                Set.of(1L)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(editReq, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}

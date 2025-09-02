package com.example.demo;

import com.example.demo.dto.ArticleCreateRequest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleCreationTest extends AbstractIntegrationTest {

    @Test
    @DataSet({"datasets/person.yml", "datasets/article/category.yml", "datasets/article/tag.yml"})
    void userCanCreateArticle() {
        String token = loginAs("moni", "moni");

        String articleUrl = "http://localhost:" + port + "/api/articles";
        ArticleCreateRequest req = new ArticleCreateRequest(
                "My first test article",
                "This is the content",
                1L,
                Set.of(1L, 2L)
        );

        HttpHeaders headers = authorizedHeaders(token);

        ResponseEntity<String> response =
                restTemplate.postForEntity(articleUrl, new HttpEntity<>(req, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("My first test article");
    }
}

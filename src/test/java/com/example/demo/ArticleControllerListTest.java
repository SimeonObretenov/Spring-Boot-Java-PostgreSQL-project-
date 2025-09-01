package com.example.demo;

import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleControllerListTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet({"datasets/person.yml",
            "datasets/article/category.yml",
            "datasets/article/tag.yml",
            "datasets/article/article.yml",
            "datasets/article/article_tag.yml"})
    void listArticlesShowsCategoryAndTags() {
        String url = "http://localhost:" + port + "/api/articles";

        ResponseEntity<String> response =
                restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).contains("First Article");
        assertThat(body).contains("Second Article");
        assertThat(body).contains("Moni Test");
        assertThat(body).contains("Tech");
        assertThat(body).contains("Spring");
        assertThat(body).contains("Java");

    }
}

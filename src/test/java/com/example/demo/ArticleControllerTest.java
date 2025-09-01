package com.example.demo;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.LoginRequest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleControllerTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DataSet({"datasets/person.yml", "datasets/article/category.yml", "datasets/article/tag.yml"})
    void userCanCreateArticle() {
        String loginUrl = "http://localhost:" + port + "/login";
        LoginRequest loginReq = new LoginRequest("moni", "moni");

        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginReq, loginHeaders);

        ResponseEntity<String> loginResp =
                restTemplate.postForEntity(loginUrl, loginEntity, String.class);

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResp.getBody();
        assertThat(token).contains("token");

        String articleUrl = "http://localhost:" + port + "/api/articles";
        ArticleCreateRequest req = new ArticleCreateRequest(
                "My first test article",
                "This is the content",
                1L,
                Set.of(1L, 2L)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.replace("{\"token\":\"", "").replace("\"}", ""));

        HttpEntity<ArticleCreateRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(articleUrl, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("My first test article");
    }
}

package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.spring.api.DBRider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DBRider
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    public ConnectionHolder connectionHolder = () -> dataSource.getConnection();


    protected String loginAs(String username, String password) {
        String url = "http://localhost:" + port + "/login";
        LoginRequest req = new LoginRequest(username, password);

        var resp = restTemplate.postForEntity(url, req, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Login failed for user: " + username);
        }

        return resp.getBody().replace("{\"token\":\"", "").replace("\"}", "");
    }


    protected HttpHeaders authorizedHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }
}

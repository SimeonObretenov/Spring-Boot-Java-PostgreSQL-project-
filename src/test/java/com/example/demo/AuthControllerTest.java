package com.example.demo;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DBUnit(alwaysCleanBefore = true, disableSequenceFiltering = true)
class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    @DataSet("datasets/person.yml")
    void loginReturnsJwtToken() {
        String token = loginAs("moni", "moni");
        assertThat(token).isNotBlank();
    }

    @Test
    @DataSet("datasets/person.yml")
    void loginFailsWithWrongPassword() {
        String url = "http://localhost:" + port + "/login";

        var badReq = new com.example.demo.dto.LoginRequest("moni", "wrongpw");
        var resp = restTemplate.postForEntity(url, badReq, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.ResetPasswordRequest;
import com.example.demo.entity.Person;
import com.example.demo.entity.Role;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PersonRepository personRepository;
    @Autowired private ArticleRepository articleRepository;

    @BeforeAll
    void clearDatabase() {
        articleRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("johndoe", "securepassword", "John Doe");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void shouldFailToRegisterUserWithDuplicateUsername() throws Exception {
        personRepository.save(new Person(null, "Jane Doe", "janedup", "pass", true, Role.USER));

        RegisterRequest duplicateRequest = new RegisterRequest("janedup", "pass", "Jane Doe");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        RegisterRequest register = new RegisterRequest("marksmith", "mypassword", "Mark Smith");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest("marksmith", "mypassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldRejectLoginWithBadCredentials() throws Exception {
        LoginRequest login = new LoginRequest("nonexistent", "wrong");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        RegisterRequest register = new RegisterRequest("resetUser", "oldpass", "Reset Guy");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        ResetPasswordRequest resetRequest = new ResetPasswordRequest("resetUser", "newpass");
        mockMvc.perform(put("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));
    }

    @Test
    void shouldDeactivateAndPreventLogin() throws Exception {
        RegisterRequest register = new RegisterRequest("inactiveUser", "somepass", "Inactive Person");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/deactivate")
                        .param("username", "inactiveUser")
                        .param("password", "somepass"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deactivated successfully"));

        LoginRequest login = new LoginRequest("inactiveUser", "somepass");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void shouldReactivateUserSuccessfully() throws Exception {
        RegisterRequest register = new RegisterRequest("inactiveUser", "somepass", "Inactive Person");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/deactivate")
                        .param("username", "inactiveUser")
                        .param("password", "somepass"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/reactivate")
                        .param("username", "inactiveUser")
                        .param("password", "somepass"))
                .andExpect(status().isOk())
                .andExpect(content().string("User reactivated successfully"));

        LoginRequest login = new LoginRequest("inactiveUser", "somepass");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }


    @Test
    void shouldRejectResetPasswordForNonexistentUser() throws Exception {
        ResetPasswordRequest reset = new ResetPasswordRequest("ghostUser", "whatever");

        mockMvc.perform(put("/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectReactivateWithWrongPassword() throws Exception {
        mockMvc.perform(put("/reactivate")
                        .param("username", "inactiveUser")
                        .param("password", "wrongpass"))
                .andExpect(status().isForbidden());
    }
}

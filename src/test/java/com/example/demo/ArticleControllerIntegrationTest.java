package com.example.demo;

import com.example.demo.dto.ArticleCreateRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Category;
import com.example.demo.entity.Tag;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.repository.TagRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ArticleControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ArticleRepository articleRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private PersonRepository personRepository;

    @BeforeAll
    void clearDatabase() {
        articleRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
        personRepository.deleteAll();
    }

    private String registerAndLogin(String username, String password, String name) throws Exception {
        RegisterRequest register = new RegisterRequest(username, password, name);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest(username, password);
        String json = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }

    private long ensureCategory(String name) {
        Category c = new Category(name);
        return categoryRepository.save(c).getId();
    }

    private Set<Long> ensureTags(String... names) {
        Set<Long> ids = new HashSet<>();
        for (String n : names) {
            Tag t = new Tag(n);
            ids.add(tagRepository.save(t).getId());
        }
        return ids;
    }


    @Test
    void shouldCreateArticleWhenAuthenticated() throws Exception {
        String token = registerAndLogin("writer1", "pass123", "Writer One");
        long catId = ensureCategory("Tech");
        Set<Long> tagIds = ensureTags("Java", "Spring");

        ArticleCreateRequest req = new ArticleCreateRequest();
        req.setTitle("Hello Spring");
        req.setContent("Spring Boot + JWT + JPA");
        req.setCategoryId(catId);
        req.setTagIds(tagIds);

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Hello Spring"))
                .andExpect(jsonPath("$.content").value("Spring Boot + JWT + JPA"))
                .andExpect(jsonPath("$.categoryName").value("Tech"))
                .andExpect(jsonPath("$.authorName").value("Writer One"))
                .andExpect(jsonPath("$.tagNames").isArray())
                .andExpect(jsonPath("$.tagNames.length()").value(2));
    }


    @Test
    void shouldUpdateArticleById() throws Exception {
        String token = registerAndLogin("writer2", "pass123", "Writer Two");
        long catA = ensureCategory("Programming");
        long catB = ensureCategory("Backend");
        Set<Long> tagsA = ensureTags("JPA", "Hibernate");
        Set<Long> tagsB = ensureTags("JWT", "Security");

        ArticleCreateRequest create = new ArticleCreateRequest();
        create.setTitle("Initial Title");
        create.setContent("Initial Content");
        create.setCategoryId(catA);
        create.setTagIds(tagsA);

        String createdJson = mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long articleId = objectMapper.readTree(createdJson).get("id").asLong();

        ArticleCreateRequest update = new ArticleCreateRequest();
        update.setTitle("Updated Title");
        update.setContent("Updated Content");
        update.setCategoryId(catB);
        update.setTagIds(tagsB);

        mockMvc.perform(put("/articles/{id}", articleId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(articleId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"))
                .andExpect(jsonPath("$.categoryName").value("Backend"))
                .andExpect(jsonPath("$.tagNames.length()").value(2));
    }


    @Test
    void shouldDeleteArticleById() throws Exception {
        String token = registerAndLogin("writer3", "pass123", "Writer Three");
        long cat = ensureCategory("General");
        Set<Long> tags = ensureTags("Misc");

        ArticleCreateRequest create = new ArticleCreateRequest();
        create.setTitle("To be deleted");
        create.setContent("Soon gone");
        create.setCategoryId(cat);
        create.setTagIds(tags);

        String createdJson = mockMvc.perform(post("/articles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        long articleId = objectMapper.readTree(createdJson).get("id").asLong();

        mockMvc.perform(delete("/articles/{id}", articleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThat(articleRepository.findById(articleId)).isEmpty();
    }


    @Test
    void shouldPaginateBlogArticlesTenPerPage() throws Exception {
        String token = registerAndLogin("blogger", "pass123", "Blog Person");
        long cat = ensureCategory("Blog");
        Set<Long> tags = ensureTags("Tag1", "Tag2");

        for (int i = 1; i <= 12; i++) {
            ArticleCreateRequest req = new ArticleCreateRequest();
            req.setTitle("Post " + i);
            req.setContent("Content " + i);
            req.setCategoryId(cat);
            req.setTagIds(tags);

            mockMvc.perform(post("/articles")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/articles/blog").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].author").value("Blog Person"))
                .andExpect(jsonPath("$[0].tags").isArray())
                .andExpect(jsonPath("$[0].categories[0]").value("Blog"));

        mockMvc.perform(get("/articles/blog").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}

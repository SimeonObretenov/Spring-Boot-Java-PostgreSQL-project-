    package com.example.demo.controller;

    import com.example.demo.dto.ArticleBlogResponse;
    import com.example.demo.dto.ArticleCreateRequest;
    import com.example.demo.dto.ArticleResponse;
    import com.example.demo.interfaces.articles_help.ArticleCreatorInterface;
    import com.example.demo.interfaces.articles_help.ArticleDeletionInterface;
    import com.example.demo.interfaces.articles_help.ArticleEditorInterface;
    import com.example.demo.interfaces.articles_help.ArticleReaderInterface;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/articles")
    @RequiredArgsConstructor
    public class ArticleController {

        private final ArticleCreatorInterface creator;
        private final ArticleReaderInterface reader;
        private final ArticleEditorInterface updater;
        private final ArticleDeletionInterface deleter;

        @Operation(summary = "Create a new article",
                description = "Creates a new article with title, content, category, and optional tags")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Article successfully created"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "403", description = "Not authorized to create")
        })
        @PostMapping
        public ArticleResponse create(@RequestBody @Valid ArticleCreateRequest req) {
            return creator.create(req);
        }

        @Operation(summary = "List articles (blog view)",
                description = "Lists all articles with pagination, showing author, tags, and categories")
        @ApiResponse(responseCode = "200", description = "List retrieved successfully")
        @GetMapping
        public List<ArticleBlogResponse> list(@RequestParam(defaultValue = "0") int page) {
            return reader.listBlog(page);
        }

        @Operation(summary = "Get one article",
                description = "Fetches full article details (title, content, category, tags, author) by ID")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Article found"),
                @ApiResponse(responseCode = "404", description = "Article not found")
        })
        @GetMapping("/{id}")
        public ArticleResponse getOne(@PathVariable Long id) {
            return reader.getOne(id);
        }

        @Operation(summary = "Update an article",
                description = "Updates title, content, category, and tags of an existing article. "
                        + "Requires WRITE permission (enforced in the service layer).")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Article successfully updated"),
                @ApiResponse(responseCode = "403", description = "Not allowed to update this article"),
                @ApiResponse(responseCode = "404", description = "Article not found")
        })
        @PutMapping("/{id}")
        public ArticleResponse update(@PathVariable Long id,
                                      @RequestBody @Valid ArticleCreateRequest req) {
            return updater.update(id, req);
        }

        @Operation(summary = "Delete an article",
                description = "Deletes an article by ID. Requires DELETE permission "
                        + "(enforced in the deletion service layer).")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Article successfully deleted"),
                @ApiResponse(responseCode = "403", description = "Not allowed to delete this article"),
                @ApiResponse(responseCode = "404", description = "Article not found")
        })
        @DeleteMapping("/{id}")
        public void delete(@PathVariable Long id) {
            deleter.delete(id);
        }
    }

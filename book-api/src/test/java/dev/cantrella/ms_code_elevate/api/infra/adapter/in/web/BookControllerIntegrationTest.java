package dev.cantrella.ms_code_elevate.api.infra.adapter.in.web;

import com.redis.testcontainers.RedisContainer;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.entity.BookEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @Container
    static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("spring.security.disabled", () -> "true");
    }

    @BeforeEach
    void setup() {
        mongoTemplate.dropCollection(BookEntity.class);
        BookEntity book1 = BookEntity.builder()
                .id("1")
                .title("Livro 1")
                .author("Autor 1")
                .mainGenre("Art")
                .build();
        BookEntity book2 = BookEntity.builder()
                .id("2")
                .title("Livro 2")
                .author("Autor 2")
                .mainGenre("Aventura")
                .build();
        mongoTemplate.insertAll(List.of(book1, book2));
    }

    @Test
    void listAll_shouldReturnPaginatedBooks() throws Exception {
        mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void find_shouldReturnBookById() throws Exception {
        mockMvc.perform(get("/books/1")
                        .header("x-user-id", "user123")) // Header ainda é necessário para cache
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void find_shouldReturnNotFoud() throws Exception {
        mockMvc.perform(get("/books/3")
                        .header("x-user-id", "user123")) // Header ainda é necessário para cache
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Book with id 3 not found."));
    }

    @Test
    void listByGenre_shouldReturnFilteredBooks() throws Exception {
        mockMvc.perform(get("/books/genre/Art"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mainGenre").value("Art"));
    }

    @Test
    void listBooksByAuthor_shouldReturnFilteredBooks() throws Exception {
        mockMvc.perform(get("/books/author/Autor 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].author").value("Autor 1"));
    }

    @Test
    void recentlyViewed_shouldReturnBooksFromCache() throws Exception {
        mockMvc.perform(get("/books/1")
                .header("x-user-id", "user123"));

        mockMvc.perform(get("/books/viewed/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}
package dev.cantrella.ms_code_elevate.api.infra.adapter.out.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheAdapterTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisCacheAdapter redisCacheAdapter;

    @Test
    void get_shouldReturnDeserializedObject_whenKeyExists() throws JsonProcessingException {
        String key = "testKey";
        Book expectedBook = Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build();
        String cachedJson = "{\"id\":\"1\",\"title\":\"Book 1\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, Book.class)).thenReturn(expectedBook);

        Book result = redisCacheAdapter.get(key, Book.class);

        assertEquals(expectedBook, result);
    }

    @Test
    void get_shouldReturnNull_whenKeyDoesNotExist() {
        String key = "nonExistentKey";
        when(valueOperations.get(key)).thenReturn(null);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        Book result = redisCacheAdapter.get(key, Book.class);

        assertNull(result);
    }

    @Test
    void get_shouldReturnNull_whenJsonProcessingExceptionOccurs() throws JsonProcessingException {
        String key = "badJsonKey";
        String cachedJson = "invalid json";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, Book.class)).thenThrow(JsonProcessingException.class);

        Book result = redisCacheAdapter.get(key, Book.class);

        assertNull(result);
    }

    @Test
    void put_shouldSerializeAndStoreObject() throws JsonProcessingException {
        String key = "testKey";
        Book book = Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build();
        String bookJson = "{\"id\":\"1\"}";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(book)).thenReturn(bookJson);

        redisCacheAdapter.put(key, book);

        verify(valueOperations).set(key, bookJson);
    }

    @Test
    void put_shouldHandleJsonProcessingException() throws JsonProcessingException {
        String key = "testKey";
        Book book = Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(book)).thenThrow(JsonProcessingException.class);

        redisCacheAdapter.put(key, book);

        verify(valueOperations, never()).set(anyString(), anyString());
    }

    @Test
    void addBookAccess_shouldAddBookToUserRecentList() throws JsonProcessingException {
                String userId = "user1";
        Book book = Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build();
        String bookJson = "{\"id\":\"1\"}";
        when(objectMapper.writeValueAsString(book)).thenReturn(bookJson);
        when(redisTemplate.opsForList()).thenReturn(mock(org.springframework.data.redis.core.ListOperations.class));
        when(redisTemplate.opsForHash()).thenReturn(mock(org.springframework.data.redis.core.HashOperations.class));

        redisCacheAdapter.addBookAccess(userId, book);

        verify(redisTemplate.opsForList()).remove(anyString(), anyLong(), anyString());
        verify(redisTemplate.opsForList()).leftPush(anyString(), anyString());
        verify(redisTemplate.opsForHash()).put(anyString(), anyString(), anyString());
    }

    @Test
    void getRecentBooks_shouldReturnListOfBooks() throws JsonProcessingException {
        String userId = "user1";
        List<String> bookIds = List.of("1", "2");
        List<Object> bookJsons = List.of("{\"id\":\"1\"}", "{\"id\":\"2\"}");
        Book book1 = Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build();
        Book book2 = Book.builder()
                .id("2")
                .title("Book 2")
                .author("Author 2")
                .mainGenre("Fiction")
                .build();
        when(redisTemplate.opsForList()).thenReturn(mock(org.springframework.data.redis.core.ListOperations.class));
        when(redisTemplate.opsForList().range(anyString(), anyLong(), anyLong())).thenReturn(bookIds);
        when(redisTemplate.opsForHash()).thenReturn(mock(org.springframework.data.redis.core.HashOperations.class));
        when(redisTemplate.opsForHash().multiGet(anyString(), any())).thenReturn(bookJsons);
        when(objectMapper.readValue(bookJsons.get(0).toString(), Book.class)).thenReturn(book1);
        when(objectMapper.readValue(bookJsons.get(1).toString(), Book.class)).thenReturn(book2);

        List<Book> result = redisCacheAdapter.getRecentBooks(userId);

        assertEquals(2, result.size());
        assertEquals(book1, result.get(0));
        assertEquals(book2, result.get(1));
    }

    @Test
    void getPagedBooks_shouldReturnPageFromCache() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 10);
        String cacheKey = "books:search:sort=UNSORTED:page=0:size=10";
        String cachedPageJson = "{\"content\":[{\"id\":\"1\"}],\"totalElements\":1}";
        JsonNode jsonNode = mock(JsonNode.class);
        List<Book> content = List.of(Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(cacheKey)).thenReturn(cachedPageJson);
        when(objectMapper.readValue(cachedPageJson, JsonNode.class)).thenReturn(jsonNode);
        when(jsonNode.get("content")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("content").toString()).thenReturn("[{\"id\":\"1\"}]");
        when(jsonNode.get("totalElements")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("totalElements").asLong()).thenReturn(1L);
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(content);

        Page<Book> result = redisCacheAdapter.getPagedBooks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(content, result.getContent());
    }

    @Test
    void putPagedBooks_shouldSerializeAndStorePage() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> content = List.of(Book.builder()
                .id("1")
                .title("Book 1")
                .author("Author 1")
                .mainGenre("Fiction")
                .build());
        Page<Book> page = new PageImpl<>(content, pageable, 1);
        String pageJson = "serializedPage";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(page)).thenReturn(pageJson);

        redisCacheAdapter.putPagedBooks(pageable, page);

        verify(valueOperations).set(anyString(), eq(pageJson), eq(Duration.ofMinutes(10)));
    }
}
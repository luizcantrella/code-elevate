package dev.cantrella.ms_code_elevate.api.infra.adapter.out.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAdapter implements CachePort {

    private static final int MAX_RECENT_BOOKS = 10;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> type) {
        try{
            var cachedBook = redisTemplate.opsForValue().get(key);
            if(cachedBook != null) {
                return objectMapper.readValue(cachedBook, type);
            }
        } catch (JsonProcessingException e) {
            log.error("Error trying to parse cache object while getting by key.");
        }
        return null;
    }

    @Override
    public void put(String key, Object value) {
        try{
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            log.error("Error trying to parse to string the object: {}", value);
        }

    }

    @Override
    public void addBookAccess(String userId, Book livro) {
        String listKey = getUserListKey(userId);
        String hashKey = getUserHashKey(userId);
        String bookId = livro.getId();
        redisTemplate.opsForList().remove(listKey, 0, bookId);
        redisTemplate.opsForList().leftPush(listKey, bookId);

        try{
            String bookJson = objectMapper.writeValueAsString(livro);
            redisTemplate.opsForHash().put(hashKey, bookId, bookJson);
            Long size = redisTemplate.opsForList().size(listKey);
            if (size != null && size > MAX_RECENT_BOOKS) {
                String removedBookId = redisTemplate.opsForList().rightPop(listKey);
                if (removedBookId != null) {
                    redisTemplate.opsForHash().delete(hashKey, removedBookId);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error trying to parse book to json while saving in cache.");
        }

    }

    @Override
    public List<Book> getRecentBooks(String userId) {
        String listKey = getUserListKey(userId);
        String hashKey = getUserHashKey(userId);
        List<String> bookIds = redisTemplate.opsForList().range(listKey, 0, MAX_RECENT_BOOKS - 1);
        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> bookJsons = redisTemplate.opsForHash().multiGet(hashKey, new ArrayList<>(bookIds));
        List<Book> books = new ArrayList<>(bookIds.size());
        if (bookJsons != null) {
            for (Object bookJson : bookJsons) {
                if (bookJson instanceof String) {
                    try {
                        Book book = objectMapper.readValue((String) bookJson, Book.class);
                        books.add(book);
                    } catch (JsonProcessingException e) {
                        log.error("Error trying to parse the object: {}", bookJson);
                    }
                }
            }
        }
        return books;
    }

    @Override
    public Page<Book> getPagedBooks(Pageable pageable) {
        String cacheKey = generateCacheKey(pageable);
        String cachedPageJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedPageJson != null) {
            log.info("Cache hit, key: {}", cacheKey);
            try {
                JsonNode pageFromCache = objectMapper.readValue(cachedPageJson, JsonNode.class);
                var content = objectMapper.readValue(pageFromCache.get("content").toString()
                        , new TypeReference<List<Book>>(){});
                long total = pageFromCache.get("totalElements").asLong();
                return new PageImpl<>(content,pageable, total);
            } catch (JsonProcessingException e) {
                log.error("Erro serializing page from cache");
            }
        }
        return null;
    }

    @Override
    public void putPagedBooks(Pageable pageable, Page<Book> page) {
        String cacheKey = generateCacheKey(pageable);
        try {
            String pageJson = objectMapper.writeValueAsString(page);
            redisTemplate.opsForValue().set(cacheKey, pageJson, Duration.ofMinutes(10));
        } catch (JsonProcessingException e) {
            log.error("Error deserializing page to save in cache");
        }
    }

    private String generateCacheKey(Pageable pageable) {
        return String.format("books:search:sort=%s:page=%d:size=%d",
                pageable.getSort().toString(),
                pageable.getPageNumber(),
                pageable.getPageSize());
    }

    private String getUserListKey(String userId) {
        return "user:" + userId + ":recentBookIds";
    }

    private String getUserHashKey(String userId) {
        return "user:" + userId + ":recentBookDetails";
    }

}

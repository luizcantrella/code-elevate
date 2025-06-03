package dev.cantrella.ms_code_elevate.api.application.ports.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CachePort {

    <T> T get(String key, Class<T> type);
    void put(String key, Object value);
    void addBookAccess(String userId, Book livro);
    List<Book> getRecentBooks(String userId);
    Page<Book> getPagedBooks(Pageable pageable);
    void putPagedBooks(Pageable pageable, Page<Book> page);
}
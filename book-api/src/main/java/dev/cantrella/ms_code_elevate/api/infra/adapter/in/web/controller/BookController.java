package dev.cantrella.ms_code_elevate.api.infra.adapter.in.web.controller;

import dev.cantrella.ms_code_elevate.api.application.ports.in.*;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/books")
public class BookController {

    private final GetBooksUseCase getBooksUseCase;
    private final GetBookByIdUseCase getBookByIdUseCase;
    private final ListBooksByGenreUseCase listBooksByGenreUseCase;
    private final ListBooksByAuthor listBooksByAuthor;
    private final ListRecentlyViewedBooksByUserUseCase listRecentlyViewedBooksByUserUseCase;
    @GetMapping
    public ResponseEntity<Page<Book>> listAll(Pageable pageable) {
        Page<Book> books = getBooksUseCase.execute(pageable);
        return ResponseEntity.ok(books);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Book> find(@PathVariable String id, @RequestHeader("x-user-id") String userId) {
        var book = getBookByIdUseCase.execute(id, userId);
        return ResponseEntity.ok(book);
    }

    @GetMapping(path = "/genre/{value}")
    public ResponseEntity<List<Book>> listByGenre(@PathVariable String value) {
        var books = listBooksByGenreUseCase.execute(value);
        return ResponseEntity.ok(books);
    }

    @GetMapping(path = "/author/{value}")
    public ResponseEntity<List<Book>> listBooksByAuthor(@PathVariable String value) {
        var books = listBooksByAuthor.execute(value);
        return ResponseEntity.ok(books);
    }

    @GetMapping(path = "/viewed/{userId}")
    public ResponseEntity<List<Book>> recentlyViewed(@PathVariable("userId") String userId) {
        var books = listRecentlyViewedBooksByUserUseCase.execute(userId);
        return ResponseEntity.ok(books);
    }
}

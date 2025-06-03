package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.in.ListBooksByAuthor;
import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListBooksByAuthorImpl implements ListBooksByAuthor {

    private final BookRepositoryPort bookRepositoryPort;

    @Override
    @Cacheable(value = "booksByAuthor")
    public List<Book> execute(String author) {
        return bookRepositoryPort.findByAuthor(author);
    }
}

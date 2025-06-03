package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.in.ListBooksByGenreUseCase;
import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListBooksByGenreUseCaseImpl implements ListBooksByGenreUseCase {
    private final BookRepositoryPort bookRepositoryPort;

    @Override
    @Cacheable(value = "booksByGenre")
    public List<Book> execute(String value) {
        return bookRepositoryPort.findByGenre(value);
    }
}

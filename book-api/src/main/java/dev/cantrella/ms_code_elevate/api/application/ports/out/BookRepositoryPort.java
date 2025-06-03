package dev.cantrella.ms_code_elevate.api.application.ports.out;

import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {

    List<Book> findByAuthor(String author);

    Optional<Book> findById(String id);

    Page<Book> findAll(Pageable pageable);

    List<Book> findByGenre(String genre);
}

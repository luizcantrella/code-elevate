package dev.cantrella.ms_code_elevate.api.application.ports.in;

import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetBooksUseCase {

    Page<Book> execute(Pageable pageable);
}

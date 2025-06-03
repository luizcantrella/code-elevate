package dev.cantrella.ms_code_elevate.api.application.ports.in;

import dev.cantrella.ms_code_elevate.api.domain.model.Book;

import java.util.List;

public interface ListRecentlyViewedBooksByUserUseCase {

    List<Book> execute(String userId);
}

package dev.cantrella.ms_code_elevate.api.application.ports.in;

import dev.cantrella.ms_code_elevate.api.domain.model.Book;

public interface GetBookByIdUseCase {

    Book execute(String id, String userId);
}

package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.exception.BookNotFoundException;
import dev.cantrella.ms_code_elevate.api.application.ports.in.GetBookByIdUseCase;
import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBookByIdUseCaseImpl implements GetBookByIdUseCase {

    private final BookRepositoryPort bookRepositoryPort;
    private final CachePort cachePort;
    @Override
    public Book execute(String id, String userId) {
        Book bookFound = cachePort.get(id, Book.class);
        if (bookFound != null) {
            cachePort.addBookAccess(userId, bookFound);
            return bookFound;
        }
        bookFound = bookRepositoryPort.findById(id).orElseThrow(()-> new BookNotFoundException(id));
        cachePort.put(bookFound.getId(), bookFound);
        cachePort.addBookAccess(userId, bookFound);
        return bookFound;
    }
}

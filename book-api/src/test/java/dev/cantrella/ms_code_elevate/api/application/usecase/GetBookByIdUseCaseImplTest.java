package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.exception.BookNotFoundException;
import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBookByIdUseCaseImplTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;
    @Mock
    private CachePort cachePort;
    @InjectMocks
    private GetBookByIdUseCaseImpl getBookByIdUseCase;

    @Test
    void execute_shouldReturnBookFromCache_whenBookIsInCache() {
        String bookId = "123";
        String userId = "user1";
        Book cachedBook = new Book();
        when(cachePort.get(bookId, Book.class)).thenReturn(cachedBook);

        Book result = getBookByIdUseCase.execute(bookId, userId);

        assertEquals(cachedBook, result);
        verify(cachePort).addBookAccess(userId, cachedBook);
        verify(bookRepositoryPort, never()).findById(any());
    }

    @Test
    void execute_shouldReturnBookFromRepository_whenBookIsNotInCache() {
        String bookId = "123";
        String userId = "user1";
        Book repositoryBook = new Book();
        repositoryBook.setId(bookId);
        when(cachePort.get(bookId, Book.class)).thenReturn(null);
        when(bookRepositoryPort.findById(bookId)).thenReturn(Optional.of(repositoryBook));

        Book result = getBookByIdUseCase.execute(bookId, userId);

        assertEquals(repositoryBook, result);
        verify(cachePort).put(bookId, result);
        verify(cachePort).addBookAccess(userId, result);
    }

    @Test
    void execute_shouldThrowBookNotFoundException_whenBookIsNotFound() {
        String bookId = "123";
        String userId = "user1";
        when(cachePort.get(bookId, Book.class)).thenReturn(null);
        when(bookRepositoryPort.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            getBookByIdUseCase.execute(bookId, userId);
        });

        verify(cachePort, never()).put(any(), any());
        verify(cachePort, never()).addBookAccess(any(), any());
    }
}
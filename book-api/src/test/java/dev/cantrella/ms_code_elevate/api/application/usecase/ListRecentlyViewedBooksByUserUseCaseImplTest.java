package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRecentlyViewedBooksByUserUseCaseImplTest {

    @Mock
    private CachePort cachePort;
    @InjectMocks
    private ListRecentlyViewedBooksByUserUseCaseImpl listRecentlyViewedBooksByUserUseCase;

    @Test
    void execute_shouldReturnBooksFromRepository() {
        String userId = "user_123";
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(cachePort.getRecentBooks(userId)).thenReturn(expectedBooks);

        List<Book> result = listRecentlyViewedBooksByUserUseCase.execute(userId);

        assertEquals(expectedBooks, result);
        verify(cachePort).getRecentBooks(userId);
        verify(cachePort, times(1)).getRecentBooks(userId);
    }
}
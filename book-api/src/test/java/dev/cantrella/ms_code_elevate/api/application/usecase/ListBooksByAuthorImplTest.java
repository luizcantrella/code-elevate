package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
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
class ListBooksByAuthorImplTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;
    @InjectMocks
    private ListBooksByAuthorImpl listBooksByAuthor;

    @Test
    void execute_shouldReturnBooksFromRepository() {
        String author = "J.R.R. Tolkien";
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        when(bookRepositoryPort.findByAuthor(author)).thenReturn(expectedBooks);

        List<Book> result = listBooksByAuthor.execute(author);

        assertEquals(expectedBooks, result);
        verify(bookRepositoryPort).findByAuthor(author);
        verify(bookRepositoryPort, times(1)).findByAuthor(author);
    }
}
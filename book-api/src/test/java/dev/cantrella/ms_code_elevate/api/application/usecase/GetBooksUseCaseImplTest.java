package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBooksUseCaseImplTest {

    @Mock
    private BookRepositoryPort bookRepositoryPort;
    @Mock
    private CachePort cachePort;
    @Mock
    private Pageable pageable;
    @InjectMocks
    private GetBooksUseCaseImpl getBooksUseCase;

    @Test
    void execute_shouldReturnBooksFromCache_whenBooksAreInCache() {
        Page<Book> cachedPage = new PageImpl<>(Collections.singletonList(new Book()));
        when(cachePort.getPagedBooks(pageable)).thenReturn(cachedPage);

        Page<Book> result = getBooksUseCase.execute(pageable);

        assertEquals(cachedPage, result);
        verify(cachePort).getPagedBooks(pageable);
        verify(bookRepositoryPort, never()).findAll(any());
        verify(cachePort, never()).putPagedBooks(any(), any());
    }

    @Test
    void execute_shouldReturnBooksFromRepositoryAndUpdateCache_whenBooksAreNotInCache() {
        Page<Book> repositoryPage = new PageImpl<>(Collections.singletonList(new Book()));
        when(cachePort.getPagedBooks(pageable)).thenReturn(null);
        when(bookRepositoryPort.findAll(pageable)).thenReturn(repositoryPage);

        Page<Book> result = getBooksUseCase.execute(pageable);

        assertEquals(repositoryPage, result);
        verify(cachePort).getPagedBooks(pageable);
        verify(bookRepositoryPort).findAll(pageable);
        verify(cachePort).putPagedBooks(pageable, repositoryPage);
    }
}
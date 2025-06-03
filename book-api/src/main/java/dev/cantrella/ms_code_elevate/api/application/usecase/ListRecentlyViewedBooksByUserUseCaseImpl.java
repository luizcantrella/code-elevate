package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.in.ListRecentlyViewedBooksByUserUseCase;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListRecentlyViewedBooksByUserUseCaseImpl implements ListRecentlyViewedBooksByUserUseCase {

    private final CachePort cachePort;

    @Override
    public List<Book> execute(String userId) {
        return cachePort.getRecentBooks(userId);
    }
}

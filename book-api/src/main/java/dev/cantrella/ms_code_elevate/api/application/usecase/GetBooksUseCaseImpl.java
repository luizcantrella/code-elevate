package dev.cantrella.ms_code_elevate.api.application.usecase;

import dev.cantrella.ms_code_elevate.api.application.ports.in.GetBooksUseCase;
import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.application.ports.out.CachePort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class GetBooksUseCaseImpl implements GetBooksUseCase {

    private final BookRepositoryPort bookRepositoryPort;
    private final CachePort cachePort;

    @Override
    public Page<Book> execute(Pageable pageable) {
        Page<Book> page = cachePort.getPagedBooks(pageable);
        if(page != null) {
            return page;
        }
        log.info("Cache miss.");
        page = bookRepositoryPort.findAll(pageable);
        cachePort.putPagedBooks(pageable, page);
        return page;
    }
}

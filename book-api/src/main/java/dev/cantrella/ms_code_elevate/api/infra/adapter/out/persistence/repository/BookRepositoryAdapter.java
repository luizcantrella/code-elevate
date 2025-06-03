package dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.repository;

import dev.cantrella.ms_code_elevate.api.application.ports.out.BookRepositoryPort;
import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.entity.BookEntity;
import dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final BookMongoRepository repository;
    private final MongoTemplate mongoTemplate;
    private final BookMapper mapper;
    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findByAuthorContainingIgnoreCase(author).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        Query query = new Query();
        long total = mongoTemplate.count(query, BookEntity.class);
        query.with(pageable);
        List<BookEntity> results = mongoTemplate.find(query, BookEntity.class);
        return new PageImpl<>(results.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList()),
                pageable, total);
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return repository.findByMainGenreContainingIgnoreCaseOrSubGenreContainingIgnoreCase(genre, genre).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

}

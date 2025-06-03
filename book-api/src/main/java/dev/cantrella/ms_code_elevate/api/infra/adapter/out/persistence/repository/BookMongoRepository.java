package dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.repository;

import dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.entity.BookEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookMongoRepository extends MongoRepository<BookEntity, String> {

    List<BookEntity> findByAuthorContainingIgnoreCase(String author);

    List<BookEntity> findByMainGenreContainingIgnoreCaseOrSubGenreContainingIgnoreCase(String mainGenre, String subGenre);
}

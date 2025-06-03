package dev.cantrella.ms_code_elevate.processor.repository;

import dev.cantrella.ms_code_elevate.processor.entity.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
}

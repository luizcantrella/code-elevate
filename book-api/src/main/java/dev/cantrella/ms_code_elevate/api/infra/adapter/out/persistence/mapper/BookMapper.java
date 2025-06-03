package dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.mapper;

import dev.cantrella.ms_code_elevate.api.domain.model.Book;
import dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toDomain(BookEntity entity) {
        if (entity == null) {
            return null;
        }
        return Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .urls(entity.getUrls())
                .price(entity.getPrice())
                .author(entity.getAuthor())
                .mainGenre(entity.getMainGenre())
                .subGenre(entity.getSubGenre())
                .numberOfPeopleRated(entity.getNumberOfPeopleRated())
                .rating(entity.getRating())
                .build();
    }

}

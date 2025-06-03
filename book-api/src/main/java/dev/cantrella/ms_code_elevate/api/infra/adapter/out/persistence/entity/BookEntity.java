package dev.cantrella.ms_code_elevate.api.infra.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity {

    @Id
    private String id;
    private String title;
    private String author;
    private String mainGenre;
    private String subGenre;
    private String type;
    private String price;
    private Double rating;
    private Integer numberOfPeopleRated;
    private String urls;

}
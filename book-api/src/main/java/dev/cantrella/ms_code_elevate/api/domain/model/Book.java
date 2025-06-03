package dev.cantrella.ms_code_elevate.api.domain.model;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
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

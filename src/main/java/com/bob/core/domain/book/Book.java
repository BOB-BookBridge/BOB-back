package com.bob.core.domain.book;

import java.time.LocalDate;

import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.bob.core.domain.AbstractEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book extends AbstractEntity {

    private String isbn;

    private String title;

    private String author;

    private String description;

    private Integer priceStandard;

    private String cover;

    private LocalDate pubDate;

    public static Book createBook(
        String isbn, String title, String author,
        String description, Integer priceStandard, String cover, LocalDate pubDate
    ) {
        return Book.builder()
            .isbn(isbn)
            .title(title)
            .author(author)
            .description(description)
            .priceStandard(priceStandard)
            .cover(cover)
            .pubDate(pubDate)
            .build();
    }
}

package com.bob.core.post.application.port.result;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record PostBook(Long id, String isbn, String title, String author, String description, LocalDate pubDate) {

}

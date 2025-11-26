package com.bob.core.application.post.port.out.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record RegisterBookcaseRequest(
    UUID memberId, String status, String isbn, String title, String author,
    String description, Integer priceStandard, String cover, LocalDate pubDate
) {

}

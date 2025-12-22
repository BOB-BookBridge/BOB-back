package com.bob.core.post.application.dto.command;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Builder;

import com.bob.core.post.application.port.out.request.RegisterBookcaseRequest;

@Builder
public record CreatePostCommand(
    UUID memberId,
    Integer categoryId,
    String description,
    String bookStatus,
    String bookIsbn,
    String bookTitle,
    String bookAuthor,
    String bookDescription,
    Integer bookPriceStandard,
    String bookCover,
    LocalDate bookPubDate,
    List<String> fileNames,
    boolean wishOnly
) {

    public RegisterBookcaseRequest toRegisterItemRequest() {
        return RegisterBookcaseRequest.builder()
            .memberId(memberId)
            .status(bookStatus)
            .isbn(bookIsbn)
            .title(bookTitle)
            .author(bookAuthor)
            .description(bookDescription)
            .priceStandard(bookPriceStandard)
            .cover(bookCover)
            .pubDate(bookPubDate)
            .build();
    }
}

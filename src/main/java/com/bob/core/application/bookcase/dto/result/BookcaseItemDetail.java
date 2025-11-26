package com.bob.core.application.bookcase.dto.result;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Builder;

import com.bob.core.application.bookcase.port.result.BookcaseItemResult;
import com.bob.core.domain.bookcase.BookcaseItem;

@Builder
public record BookcaseItemDetail(
    Long id,
    Long bookId,
    String status,
    String isbn,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate,
    boolean available
) {

    public static List<BookcaseItemDetail> listFrom(List<BookcaseItem> bookcases, List<BookcaseItemResult> books) {
        if (bookcases == null || bookcases.isEmpty() || books == null || books.isEmpty())
            return List.of();

        Map<Long, BookcaseItemResult> bookMap = books.stream()
            .collect(Collectors.toMap(BookcaseItemResult::id, Function.identity(), (a, b) -> a));

        return bookcases.stream()
            .map(bc -> from(bc, bookMap.get(bc.getBookId())))
            .filter(Objects::nonNull)
            .toList();
    }

    public static BookcaseItemDetail from(BookcaseItem bookcase, BookcaseItemResult book) {
        return BookcaseItemDetail.builder()
            .id(bookcase.getId())
            .bookId(bookcase.getBookId())
            .status(bookcase.getStatus().name())
            .isbn(book.isbn())
            .title(book.title())
            .author(book.author())
            .priceStandard(book.priceStandard())
            .cover(book.cover())
            .pubDate(book.pubDate())
            .available(!bookcase.isDeleted() && bookcase.getUsageId() == null)
            .build();
    }
}

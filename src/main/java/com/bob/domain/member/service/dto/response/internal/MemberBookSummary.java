package com.bob.domain.member.service.dto.response.internal;

import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.member.entity.MemberBook;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record MemberBookSummary(
    Long id,
    String status,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static List<MemberBookSummary> listFrom(List<MemberBook> memberBooks, List<BookResponse> books) {
    if (memberBooks == null || memberBooks.isEmpty() || books == null || books.isEmpty())
      return List.of();

    Map<Long, BookResponse> bookMap = books.stream()
        .collect(Collectors.toMap(BookResponse::id, Function.identity(), (a, b) -> a));

    return memberBooks.stream()
        .map(mb -> from(mb, bookMap.get(mb.getBookId())))
        .filter(Objects::nonNull)
        .toList();
  }

  public static MemberBookSummary from(MemberBook memberBook, BookResponse book) {
    return MemberBookSummary.builder()
        .id(memberBook.getId())
        .status(memberBook.getStatus().name())
        .title(book.title())
        .author(book.author())
        .priceStandard(book.priceStandard())
        .cover(book.cover())
        .pubDate(book.pubDate())
        .build();
  }
}

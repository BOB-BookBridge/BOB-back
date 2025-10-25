package com.bob.domain.member.service.dto.response.internal;

import com.bob.domain.book.service.dto.response.BookResponse;
import com.bob.domain.member.entity.MemberWish;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record MemberWishSummary(
    Long id,
    String title,
    String author,
    Integer priceStandard,
    String cover,
    LocalDate pubDate
) {

  public static MemberWishSummary of(
      Long id,
      String title,
      String author,
      Integer priceStandard,
      String cover,
      LocalDate pubDate
  ) {
    return MemberWishSummary.builder()
        .id(id)
        .title(title)
        .author(author)
        .priceStandard(priceStandard)
        .cover(cover)
        .pubDate(pubDate)
        .build();
  }

  public static MemberWishSummary from(MemberWish wish, BookResponse book) {
    return MemberWishSummary.of(
        wish.getId(),
        book.title(),
        book.author(),
        book.priceStandard(),
        book.cover(),
        book.pubDate()
    );
  }

  public static List<MemberWishSummary> listFrom(List<MemberWish> wishes, List<BookResponse> books) {
    if (wishes.isEmpty() || books.isEmpty()) {
      return List.of();
    }

    Map<Long, BookResponse> bookMap = books.stream()
        .collect(Collectors.toMap(BookResponse::id, Function.identity(), (a, b) -> a));

    return wishes.stream()
        .map(wish -> from(wish, bookMap.get(wish.getBookId())))
        .filter(Objects::nonNull)
        .toList();
  }
}

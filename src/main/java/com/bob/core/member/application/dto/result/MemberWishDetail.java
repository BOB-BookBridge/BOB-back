package com.bob.core.member.application.dto.result;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import lombok.Builder;

import com.bob.core.member.application.port.result.MemberBookResult;
import com.bob.core.member.domain.MemberWish;

@Builder
public record MemberWishDetail(
    Long id, Long bookId, String title, String author,
    Integer priceStandard, String cover, LocalDate pubDate
) {

    public static List<MemberWishDetail> listFrom(List<MemberWish> wishes, List<MemberBookResult> books) {
        if (wishes.isEmpty())
            return List.of();

        Map<Long, MemberBookResult> bookMap = books.stream()
            .collect(toMap(MemberBookResult::id, Function.identity(), (a, b) -> a));

        return wishes.stream()
            .map((wish) -> from(wish, bookMap.get(wish.getBookId())))
            .filter(Objects::nonNull)
            .toList();
    }

    public static MemberWishDetail from(MemberWish wish, MemberBookResult book) {
        return MemberWishDetail.builder()
            .id(wish.getId())
            .bookId(wish.getBookId())
            .title(book.title())
            .author(book.author())
            .priceStandard(book.priceStandard())
            .cover(book.cover())
            .pubDate(book.pubDate())
            .build();
    }
}

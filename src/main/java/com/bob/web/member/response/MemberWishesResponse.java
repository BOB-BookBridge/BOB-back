package com.bob.web.member.response;

import com.bob.domain.member.service.dto.response.MemberWishesResult;
import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record MemberWishesResponse(

    List<WishSummary> wishes
) {

  public static MemberWishesResponse from(MemberWishesResult result) {
    return new MemberWishesResponse(result.wishes().stream()
        .map(WishSummary::from)
        .toList());
  }

  @Builder
  record WishSummary(
      Long id,
      String title,
      String author,
      Integer priceStandard,
      String cover,
      LocalDate pubDate
  ) {

    static WishSummary from(MemberWishSummary summary) {
      return WishSummary.builder()
          .id(summary.id())
          .title(summary.title())
          .author(summary.author())
          .priceStandard(summary.priceStandard())
          .cover(summary.cover())
          .pubDate(summary.pubDate())
          .build();
    }
  }
}

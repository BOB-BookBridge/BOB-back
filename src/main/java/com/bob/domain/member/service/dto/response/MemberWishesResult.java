package com.bob.domain.member.service.dto.response;

import com.bob.domain.member.service.dto.response.internal.MemberWishSummary;
import java.util.List;

public record MemberWishesResult(
    List<MemberWishSummary> wishes
) {

  public static MemberWishesResult from(List<MemberWishSummary> wishes) {
    return new MemberWishesResult(wishes);
  }
}

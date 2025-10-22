package com.bob.domain.member.service.dto.response;

import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import java.util.List;

public record MemberBooksResponse(
    List<MemberBookSummary> bookcase
) {

  public static MemberBooksResponse of(List<MemberBookSummary> books) {
    return new MemberBooksResponse(books);
  }
}

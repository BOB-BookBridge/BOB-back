package com.bob.domain.member.service.dto.response;

import com.bob.domain.member.service.dto.response.internal.MemberBookSummary;
import java.util.List;

public record MemberBooksResponse(
    List<MemberBookSummary> books
) {

  public static MemberBooksResponse of(List<MemberBookSummary> books) {
    return new MemberBooksResponse(books);
  }
}

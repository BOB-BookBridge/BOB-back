package com.bob.domain.member.service.dto.query;

import java.util.List;

public record ReadMemberBooksByIdQuery(
    List<Long> ids
) {

  public static ReadMemberBooksByIdQuery of(List<Long> ids) {
    return new ReadMemberBooksByIdQuery(ids);
  }
}

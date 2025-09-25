package com.bob.domain.member.service.dto.query;

import java.util.UUID;

public record ReadMemberBooksQuery(
    UUID memberId
) {

  public static ReadMemberBooksQuery of(UUID memberId) {
    return new ReadMemberBooksQuery(memberId);
  }
}

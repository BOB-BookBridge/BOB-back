package com.bob.domain.member.service.dto.query;

import java.util.List;
import java.util.UUID;

public record ReadMemberBooksQuery(
    UUID memberId,
    SearchKey key,
    List<Long> requires
) {

  public static ReadMemberBooksQuery of(UUID memberId) {
    return new ReadMemberBooksQuery(memberId, SearchKey.ALL, List.of());
  }

  public static ReadMemberBooksQuery of(UUID memberId, String key, List<Long> requires) {
    return new ReadMemberBooksQuery(memberId, SearchKey.of(key), requires);
  }
}

package com.bob.domain.member.service.dto.query;

import java.util.UUID;

public record ReadMemberWishesQuery(
    UUID memberId
) {

  public static ReadMemberWishesQuery of(UUID memberId) {
    return new ReadMemberWishesQuery(memberId);
  }
}

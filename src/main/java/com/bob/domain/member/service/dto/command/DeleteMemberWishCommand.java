package com.bob.domain.member.service.dto.command;

import java.util.UUID;

public record DeleteMemberWishCommand(
    UUID memberId,
    Long id
) {

  public static DeleteMemberWishCommand of(UUID memberId, Long id) {
    return new DeleteMemberWishCommand(memberId, id);
  }
}

package com.bob.domain.member.service.dto.command;

import java.util.UUID;

public record RemoveMemberBookCommand(
    UUID memberId,
    Long id
) {

  public static RemoveMemberBookCommand of(UUID memberId, Long id) {
    return new RemoveMemberBookCommand(memberId, id);
  }
}

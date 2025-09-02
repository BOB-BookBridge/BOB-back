package com.bob.global.event.application.dto.member;

import com.bob.global.event.application.dto.member.type.AccountEventType;
import java.util.UUID;

public record AccountEvent(
    UUID memberId,
    AccountEventType type
) {

  public static AccountEvent of(UUID memberId, AccountEventType type) {
    return new AccountEvent(memberId, type);
  }
}

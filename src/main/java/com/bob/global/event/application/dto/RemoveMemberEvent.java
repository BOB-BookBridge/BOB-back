package com.bob.global.event.application.dto;

import java.util.UUID;

public record RemoveMemberEvent(
    UUID memberId
) {

  public static RemoveMemberEvent of(UUID memberId) {
    return new RemoveMemberEvent(memberId);
  }
}

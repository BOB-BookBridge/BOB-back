package com.bob.global.event.sse.repository.notification;

import java.util.UUID;

public record NotiEmitterKey(
    UUID memberId
) {

  @Override
  public String toString() {
    return "noti:" + memberId;
  }
}

package com.bob.domain.notification.service.dto.query;

import java.util.UUID;

public record ReadNotificationsQuery(
    UUID memberId
) {

  public static ReadNotificationsQuery of(UUID memberId) {
    return new ReadNotificationsQuery(memberId);
  }
}

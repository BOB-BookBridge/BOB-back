package com.bob.domain.area.service.dto.query;

import java.util.UUID;

public record ReadAreaQuery(
    UUID memberId
) {

  public static ReadAreaQuery of(UUID memberId) {
    return new ReadAreaQuery(memberId);
  }
}

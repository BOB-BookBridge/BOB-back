package com.bob.domain.area.service.dto.command;

import java.util.UUID;

public record AuthenticationCommand(
    Integer emdId,
    Double lat,
    Double lon,
    UUID memberId
) {

  public boolean isGuest() {
    return memberId == null;
  }
}

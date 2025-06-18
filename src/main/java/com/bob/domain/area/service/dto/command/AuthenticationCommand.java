package com.bob.domain.area.service.dto.command;

import static com.bob.domain.area.service.dto.command.AuthenticationPurpose.SIGN_UP;

import java.util.UUID;

public record AuthenticationCommand(
    Integer emdId,
    Double lat,
    Double lon,
    AuthenticationPurpose purpose,
    UUID memberId
) {

  public boolean isGuest() {
    return memberId == null;
  }

  public boolean isSignup() {
    return purpose == SIGN_UP;
  }
}

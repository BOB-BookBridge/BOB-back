package com.bob.web.area.request;

import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import com.bob.domain.area.service.dto.command.AuthenticationPurpose;
import java.util.UUID;

public record AuthenticationRequest(
    Integer emdId,
    Double lat,
    Double lon,
    String purpose
) {

  public AuthenticationCommand toCommand(UUID memberId) {
    return new AuthenticationCommand(
        emdId,
        lat,
        lon,
        AuthenticationPurpose.valueOf(purpose),
        memberId
    );
  }
}

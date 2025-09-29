package com.bob.web.area.request;

import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import java.util.UUID;

public record AuthenticationRequest(
    Integer emdId,
    Double lat,
    Double lon
) {

  public AuthenticationCommand toCommand(UUID memberId) {
    return new AuthenticationCommand(emdId, lat, lon, memberId);
  }
}

package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.area.service.dto.command.AuthenticationCommand;
import java.util.UUID;

public class AuthenticationCommandFixture {

  public static final int DEFAULT_EMD_ID = 213;
  public static final double DEFAULT_LAT = 37.500578;
  public static final double DEFAULT_LON = 127.036991;
  public static final double OTHER_LAT = 37.730629;
  public static final double OTHER_LON = 127.060564;

  public static AuthenticationCommand defaultAuthenticationCommand() {
    return new AuthenticationCommand(DEFAULT_EMD_ID, DEFAULT_LAT, DEFAULT_LON, MEMBER_ID);
  }

  public static AuthenticationCommand defaultMismatchAuthenticationCommand() {
    return new AuthenticationCommand(DEFAULT_EMD_ID, DEFAULT_LAT + 1, DEFAULT_LON + 1, MEMBER_ID);
  }

  public static AuthenticationCommand customAuthenticationCommand(UUID memberId, Integer emdId, Double lat, Double lon) {
    return new AuthenticationCommand(emdId, lat, lon, memberId);
  }

  public static AuthenticationCommand guestCommand() {
    return new AuthenticationCommand(DEFAULT_EMD_ID, DEFAULT_LAT, DEFAULT_LON, null);
  }
}
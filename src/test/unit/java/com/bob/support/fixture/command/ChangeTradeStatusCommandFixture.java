package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;

public class ChangeTradeStatusCommandFixture {

  public static ChangeTradeStatusCommand DEFAULT_CHANGE_STATUS_COMMAND(String status) {
    return new ChangeTradeStatusCommand(MEMBER_ID, 1L, status, null);
  }

  public static ChangeTradeStatusCommand DEFAULT_CHANGE_STATUS_COMMAND_WITH_REASON(String status, String reason) {
    return new ChangeTradeStatusCommand(MEMBER_ID, 1L, status, reason);
  }
}

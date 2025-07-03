package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;

public class CreateTradeCommandFixture {

  public static CreateTradeCommand DEFAULT_CREATE_TRADE_COMMAND() {
    return new CreateTradeCommand(1L, MEMBER_ID, OTHER_MEMBER_ID);
  }
}

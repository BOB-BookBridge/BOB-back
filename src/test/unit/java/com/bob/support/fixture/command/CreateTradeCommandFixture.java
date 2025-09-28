package com.bob.support.fixture.command;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import java.util.List;

public class CreateTradeCommandFixture {

  public static final CreateTradeCommand DEFAULT_CREATE_TRADE_COMMAND =
      new CreateTradeCommand(1L, OTHER_MEMBER_ID, List.of(1L, 2L));

  public static final CreateTradeCommand SAME_MEMBER_CREATE_TRADE_COMMAND =
      new CreateTradeCommand(1L, MEMBER_ID, List.of(1L, 2L));
}

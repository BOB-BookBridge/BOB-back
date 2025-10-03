package com.bob.support.fixture.response.trade;

import static com.bob.support.fixture.response.trade.internal.TradeSummaryFixture.REQUESTED_RECEIVED_TRADE_SUMMARY1;
import static com.bob.support.fixture.response.trade.internal.TradeSummaryFixture.REQUESTED_SENT_TRADE_SUMMARY1;
import static com.bob.support.fixture.response.trade.internal.TradeSummaryFixture.RESERVED_RECEIVED_TRADE_SUMMARY2;
import static com.bob.support.fixture.response.trade.internal.TradeSummaryFixture.RESERVED_SENT_TRADE_SUMMARY2;

import com.bob.domain.trade.service.dto.response.TradesResponse;
import java.util.List;

public class TradesResponseFixture {

  public static final TradesResponse RECEIVED_TRADES_RESPONSE =
      TradesResponse.from(List.of(REQUESTED_RECEIVED_TRADE_SUMMARY1, RESERVED_RECEIVED_TRADE_SUMMARY2), 2L);

  public static final TradesResponse SENT_TRADES_RESPONSE =
      TradesResponse.from(List.of(REQUESTED_SENT_TRADE_SUMMARY1, RESERVED_SENT_TRADE_SUMMARY2), 2L);
}

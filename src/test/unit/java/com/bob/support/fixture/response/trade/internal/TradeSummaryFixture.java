package com.bob.support.fixture.response.trade.internal;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.response.PostResponseFixture.CUSTOM_POST_DETAIL_RESPONSE;

import com.bob.domain.trade.service.dto.response.internal.TradePostSummary;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;

public class TradeSummaryFixture {

  public static final TradeSummary REQUESTED_RECEIVED_TRADE_SUMMARY1 = TradeSummary.builder()
      .id(1L)
      .status("REQUESTED")
      .post(TradePostSummary.from(CUSTOM_POST_DETAIL_RESPONSE(1L, MEMBER_ID)))
      .build();

  public static final TradeSummary RESERVED_RECEIVED_TRADE_SUMMARY2 = TradeSummary.builder()
      .id(2L)
      .status("RESERVED")
      .post(TradePostSummary.from(CUSTOM_POST_DETAIL_RESPONSE(2L, MEMBER_ID)))
      .build();

  public static final TradeSummary REQUESTED_SENT_TRADE_SUMMARY1 = TradeSummary.builder()
      .id(1L)
      .status("REQUESTED")
      .post(TradePostSummary.from(CUSTOM_POST_DETAIL_RESPONSE(1L, OTHER_MEMBER_ID)))
      .build();

  public static final TradeSummary RESERVED_SENT_TRADE_SUMMARY2 = TradeSummary.builder()
      .id(2L)
      .status("RESERVED")
      .post(TradePostSummary.from(CUSTOM_POST_DETAIL_RESPONSE(2L, OTHER_MEMBER_ID)))
      .build();
}

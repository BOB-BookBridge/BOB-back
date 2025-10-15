package com.bob.support.fixture.response.trade.internal;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.trade.service.dto.response.internal.TradePostSummary;

public class TradePostSummaryFixture {

  public static TradePostSummary DEFAULT_TRADE_POST_SUMMARY = TradePostSummary.builder()
      .id(1L)
      .status("REQUESTED")
      .sellerId(MEMBER_ID)
      .sellerBookId(1L)
      .title("제목")
      .thumbnailUrl("https://image.png")
      .build();
}

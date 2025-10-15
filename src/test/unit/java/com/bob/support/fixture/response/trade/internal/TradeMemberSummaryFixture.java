package com.bob.support.fixture.response.trade.internal;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.trade.service.dto.response.internal.TradeMemberSummary;

public class TradeMemberSummaryFixture {

  public static TradeMemberSummary DEFAULT_TRADE_SELLER_SUMMARY = TradeMemberSummary.builder()
      .id(MEMBER_ID)
      .nickname("판매자")
      .profile(null)
      .build();

  public static TradeMemberSummary DEFAULT_TRADE_BUYER_SUMMARY = TradeMemberSummary.builder()
      .id(OTHER_MEMBER_ID)
      .nickname("구매자")
      .profile(null)
      .build();
}

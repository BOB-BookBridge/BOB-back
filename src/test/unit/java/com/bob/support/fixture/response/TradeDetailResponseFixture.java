package com.bob.support.fixture.response;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import java.time.LocalDateTime;

public class TradeDetailResponseFixture {

  public static final TradeDetailResponse DEFAULT_TRADE_DETAIL() {
    return TradeDetailResponse.builder()
        .id(1L)
        .postId(1L)
        .sellerId(MEMBER_ID)
        .buyerId(OTHER_MEMBER_ID)
        .status("READY")
        .updatedAt(LocalDateTime.now())
        .build();
  }
}

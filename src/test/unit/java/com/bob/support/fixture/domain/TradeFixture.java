package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TradeFixture {

  public static final Trade DEFAULT_TRADE_WITH_ID = Trade.builder()
      .id(1L)
      .postId(1L)
      .sellerId(MEMBER_ID)
      .buyerId(OTHER_MEMBER_ID)
      .status(Status.REQUESTED)
      .updatedAt(LocalDateTime.now())
      .build();

  public static Trade TRADE(Long id, Long postId, Status status) {
    return Trade.builder()
        .id(id)
        .postId(postId)
        .sellerId(MEMBER_ID)
        .buyerId(UUID.randomUUID())
        .status(status)
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public static Trade REQUESTED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, Status.REQUESTED);
  }

  public static Trade RESERVED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, Status.RESERVED);
  }

  public static Trade COMPLETED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, Status.COMPLETED);
  }

  public static Trade CANCELED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, Status.CANCELED);
  }

  public static List<Trade> DEFAULT_TRADES() {
    return List.of(REQUESTED_TRADE(1L, 1L), REQUESTED_TRADE(2L, 1L), RESERVED_TRADE(3L, 1L));
  }
}

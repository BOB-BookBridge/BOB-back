package com.bob.support.fixture.domain;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.TradeStatus;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TradeFixture {

  public static Trade DEFAULT_ID_TRADE(CreateTradeCommand command) {
    return Trade.builder()
        .id(1L)
        .postId(command.postId())
        .sellerId(command.sellerId())
        .buyerId(command.buyerId())
        .tradeStatus(TradeStatus.REQUESTED)
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public static Trade TRADE(Long id, Long postId, TradeStatus status) {
    return Trade.builder()
        .id(id)
        .postId(postId)
        .sellerId(MEMBER_ID)
        .buyerId(UUID.randomUUID())
        .tradeStatus(status)
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public static Trade REQUESTED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, TradeStatus.REQUESTED);
  }

  public static Trade RESERVED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, TradeStatus.RESERVED);
  }

  public static Trade COMPLETED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, TradeStatus.COMPLETED);
  }

  public static Trade CANCELED_TRADE(Long id, Long postId) {
    return TRADE(id, postId, TradeStatus.CANCELED);
  }

  public static List<Trade> DEFAULT_TRADES() {
    return List.of(REQUESTED_TRADE(1L, 1L), REQUESTED_TRADE(2L, 1L), RESERVED_TRADE(3L, 1L));
  }
}

package com.bob.domain.trade.service.dto.response.internal;

import com.bob.domain.trade.entity.Trade;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradeSummary(
    Long tradeId,
    String tradeStatus,
    Buyer buyer
) {

  public static TradeSummary from(Trade trade, TradeMemberSummary member) {
    return TradeSummary.builder()
        .tradeId(trade.getId())
        .tradeStatus(trade.getTradeStatus().name())
        .buyer(Buyer.of(member.id(), member.nickname(), member.profile()))
        .build();
  }

  public record Buyer(
      UUID id,
      String nickname,
      String profile
  ) {
    public static Buyer of(UUID id, String nickname, String profile) {
      return new Buyer(id, nickname, profile);
    }
  }
}

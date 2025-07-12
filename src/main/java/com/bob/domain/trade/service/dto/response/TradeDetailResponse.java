package com.bob.domain.trade.service.dto.response;

import com.bob.domain.trade.entity.Trade;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TradeDetailResponse(
    Long id,
    Long postId,
    UUID sellerId,
    UUID buyerId,
    String status,
    LocalDateTime updatedAt
) {

  public static TradeDetailResponse from(Trade trade) {
    return TradeDetailResponse.builder()
        .id(trade.getId())
        .postId(trade.getPostId())
        .sellerId(trade.getSellerId())
        .buyerId(trade.getBuyerId())
        .status(trade.getTradeStatus().name())
        .updatedAt(trade.getUpdatedAt())
        .build();
  }
}

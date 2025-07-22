package com.bob.domain.trade.entity.status;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TradeStatus {
  REQUESTED("대기"),
  RESERVED("예약"),
  COMPLETED("완료"),
  CANCELED("취소");

  private final String value;

  public boolean isProcessed() {
    return this == RESERVED || this == COMPLETED;
  }

  public String toPostStatusValue() {
    return switch (this) {
      case REQUESTED, CANCELED -> "READY";
      case RESERVED -> "IN_PROGRESS";
      case COMPLETED -> "COMPLETED";
    };
  }

  public String value() {
    return value;
  }
}

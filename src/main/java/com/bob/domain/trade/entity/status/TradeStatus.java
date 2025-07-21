package com.bob.domain.trade.entity.status;

public enum TradeStatus {
  REQUESTED,
  RESERVED,
  COMPLETED,
  CANCELED;

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

  public String toValue() {
    return switch (this) {
      case REQUESTED -> "대기";
      case RESERVED -> "예약";
      case COMPLETED -> "완료";
      case CANCELED -> "취소";
    };
  }
}

package com.bob.domain.trade.service.util;

public enum TradeMessageTemplate {

  REQUESTED_NOTI("%s님이 [%s] 게시글에 거래 요청을 보냈습니다."),
  STATUS_CHANGED_NOTI("[%s]의 거래 진행 상태가 '%s'(으)로 변경되었습니다."),
  CANCELED_WITH_REASON_NOTI("[%s]의 거래 진행 상태가 '%s'(으)로 변경되었습니다.\n\n사유: %s"),

  STATUS_CHANGED_CHAT("거래 진행 상태가 '%s'(으)로 변경되었습니다."),
  CANCELED_WITH_REASON_CHAT("거래가 취소되었습니다.\n\n사유: %s");

  private final String template;

  TradeMessageTemplate(String template) {
    this.template = template;
  }

  public String format(Object... args) {
    return String.format(template, args);
  }
}

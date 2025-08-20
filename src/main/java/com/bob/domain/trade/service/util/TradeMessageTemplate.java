package com.bob.domain.trade.service.util;

public enum TradeMessageTemplate {

  NOTI_DEFAULT("[%s]의 거래 상태가 '%s'(으)로 변경되었습니다."),
  NOTI_CANCELED_WITH_REASON("[%s]의 거래 상태가 '%s'(으)로 변경되었습니다.\n\n사유: %s"),

  CHAT_DEFAULT("거래 상태가 '%s'(으)로 변경되었습니다."),
  CHAT_CANCELED_WITH_REASON("거래가 취소되었습니다.\n\n사유: %s");

  private final String template;

  TradeMessageTemplate(String template) {
    this.template = template;
  }

  public String format(Object... args) {
    return String.format(template, args);
  }
}

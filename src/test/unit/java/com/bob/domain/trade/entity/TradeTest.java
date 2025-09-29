package com.bob.domain.trade.entity;

import static com.bob.domain.trade.entity.status.Status.RESERVED;
import static com.bob.support.fixture.domain.TradeFixture.TRADE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거래 도메인 테스트")
class TradeTest {

  @Test
  @DisplayName("거래 상태를 변경 테스트")
  void 거래_상태를_변경하면_상태와_시간이_갱신된다() {
    // given
    Trade trade = TRADE(1L, 1L, RESERVED);
    LocalDateTime now = LocalDateTime.now();

    // when
    trade.updateTradeStatus(RESERVED, now);

    // then
    assertThat(trade.getStatus()).isEqualTo(RESERVED);
    assertThat(trade.getUpdatedAt()).isEqualTo(now);
  }
}

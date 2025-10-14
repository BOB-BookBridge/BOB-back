package com.bob.domain.trade.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.trade.entity.type.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거래 물품 도메인 테스트")
class TradeItemTest {

  @Test
  void 거래_물품_생성() {
    // given
    Long tradeId = 1L;
    Long itemId = 100L;
    Owner owner = Owner.SELLER;

    // when
    TradeItem item = TradeItem.create(tradeId, itemId, owner);

    // then
    assertThat(item.getId()).isNull();
    assertThat(item.getTradeId()).isEqualTo(tradeId);
    assertThat(item.getItemId()).isEqualTo(itemId);
    assertThat(item.getOwner()).isEqualTo(owner);
  }
}
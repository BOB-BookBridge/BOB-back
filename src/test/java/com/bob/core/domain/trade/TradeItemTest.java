package com.bob.core.domain.trade;

import static com.bob.core.domain.trade.type.Owner.SELLER;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거래 물품 도메인 테스트")
class TradeItemTest {

    @Test
    void 거래_물품_생성() {
        Long itemId = 100L;

        TradeItem item = TradeItem.create(itemId, SELLER);

        assertThat(item.getItemId()).isEqualTo(itemId);
        assertThat(item.getOwner()).isEqualTo(SELLER);
        assertThat(item.getCreatedAt()).isNotNull();
    }
}

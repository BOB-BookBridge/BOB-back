package com.bob.core.domain.trade;

import static com.bob.core.domain.trade.status.Status.CANCELED;
import static com.bob.core.domain.trade.status.Status.COMPLETED;
import static com.bob.core.domain.trade.status.Status.REJECTED;
import static com.bob.core.domain.trade.status.Status.REQUESTED;
import static com.bob.core.domain.trade.status.Status.RESERVED;
import static com.bob.core.domain.trade.type.Owner.BUYER;
import static com.bob.core.domain.trade.type.Owner.SELLER;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.member.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.trade.domain.TradeFixture.createTrade;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("거래 도메인 테스트")
class TradeTest {

    @Test
    void 거래_생성() {
        Long postId = 1L;

        Trade trade = Trade.createTrade(postId, MEMBER_ID, OTHER_MEMBER_ID, false);

        assertThat(trade.getPostId()).isEqualTo(postId);
        assertThat(trade.getSellerId()).isEqualTo(MEMBER_ID);
        assertThat(trade.getBuyerId()).isEqualTo(OTHER_MEMBER_ID);
        assertThat(trade.isFar()).isFalse();
        assertThat(trade.getStatus()).isEqualTo(REQUESTED);
        assertThat(trade.getItems()).isEmpty();
    }

    @Test
    void 거래_상태_변경() {
        Trade trade = Trade.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, false);

        trade.updateStatus(RESERVED);

        assertThat(trade.getStatus()).isEqualTo(RESERVED);
    }

    @Test
    void 거래_품목_추가() {
        Trade trade = Trade.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, false);
        Long itemId = 100L;

        trade.addItem(itemId, SELLER);

        assertThat(trade.getItems()).hasSize(1);
        assertThat(trade.getItems().get(0).getItemId()).isEqualTo(itemId);
        assertThat(trade.getItems().get(0).getOwner()).isEqualTo(SELLER);
    }

    @Test
    void 거래_품목_전체_조회() {
        Trade trade = Trade.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, false);
        trade.addItem(100L, SELLER);
        trade.addItem(200L, BUYER);
        trade.addItem(300L, BUYER);

        List<Long> allItems = trade.getAllItemIds();

        assertThat(allItems).hasSize(3);
        assertThat(allItems).containsExactlyInAnyOrder(100L, 200L, 300L);
    }

    @Test
    void 거래_품목_제거() {
        Trade trade = Trade.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, false);
        trade.addItem(100L, SELLER);
        trade.addItem(200L, BUYER);
        trade.addItem(300L, BUYER);

        trade.removeItems(BUYER, List.of(200L));

        List<Long> buyerItems = trade.getItemIdsByOwner(BUYER);
        assertThat(buyerItems).containsExactly(300L);
        assertThat(trade.getItems()).hasSize(2);
    }

    @Test
    void 거래_품목_전체_제거() {
        Trade trade = Trade.createTrade(1L, MEMBER_ID, OTHER_MEMBER_ID, false);
        trade.addItem(100L, SELLER);
        trade.addItem(200L, BUYER);
        trade.addItem(300L, BUYER);

        trade.clearItems();

        assertThat(trade.getItems()).isEmpty();
        assertThat(trade.getAllItemIds()).isEmpty();
    }

    @Test
    void 거래_진행_여부() {
        assertThat(createTrade(REQUESTED).isProcessed()).isFalse();
        assertThat(createTrade(RESERVED).isProcessed()).isTrue();
        assertThat(createTrade(COMPLETED).isProcessed()).isTrue();
        assertThat(createTrade(CANCELED).isProcessed()).isFalse();
    }

    @Test
    void 거래_중단_여부() {
        assertThat(createTrade(REQUESTED).isAborted()).isFalse();
        assertThat(createTrade(REJECTED).isAborted()).isTrue();
        assertThat(createTrade(CANCELED).isAborted()).isTrue();
    }

    @Test
    void 거래_상태_확인() {
        assertThat(createTrade(RESERVED).isReserved()).isTrue();
        assertThat(createTrade(COMPLETED).isCompleted()).isTrue();
        assertThat(createTrade(REJECTED).isRejected()).isTrue();
        assertThat(createTrade(CANCELED).isCancelled()).isTrue();
    }
}

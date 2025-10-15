package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.trade.TradeItemFixture.DEFAULT_TRADE_BUYER_ITEM_1;
import static com.bob.support.fixture.domain.trade.TradeItemFixture.DEFAULT_TRADE_BUYER_ITEM_2;
import static com.bob.support.fixture.domain.trade.TradeItemFixture.DEFAULT_TRADE_SELLER_ITEM_1;
import static com.bob.support.fixture.domain.trade.TradeItemFixture.DEFAULT_TRADE_SELLER_ITEM_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.repository.TradeItemRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("거래 물품 서비스 테스트")
class TradeItemServiceTest {

  @InjectMocks
  TradeItemService tradeItemService;

  @Mock
  TradeItemRepository tradeItemRepository;

  @Mock
  TradeMemberPort memberPort;

  @Test
  void 거래_물품_생성() {
    // given
    Long tradeId = 1L;
    Owner owner = SELLER;
    List<Long> itemIds = List.of(10L, 20L, 30L);
    CreateTradeItemsCommand command = CreateTradeItemsCommand.of(tradeId, itemIds, owner);

    // when
    tradeItemService.createTradeItemsProcess(command);

    // then
    ArgumentCaptor<List<TradeItem>> captor = ArgumentCaptor.forClass(List.class);
    then(tradeItemRepository).should().saveAll(captor.capture());

    List<TradeItem> saved = captor.getValue();
    assertThat(saved).hasSize(3);
    assertThat(saved).extracting(TradeItem::getItemId).containsExactlyInAnyOrder(10L, 20L, 30L);
    assertThat(saved).allSatisfy(item -> {
      assertThat(item.getTradeId()).isEqualTo(tradeId);
      assertThat(item.getOwner()).isEqualTo(owner);
    });
  }

  @Test
  void 거래_물품_ITEM_ID_목록_조회() {
    // given
    Long tradeId = 1L;
    Owner owner = SELLER;
    List<TradeItem> rows = List.of(DEFAULT_TRADE_SELLER_ITEM_1, DEFAULT_TRADE_SELLER_ITEM_2);
    given(tradeItemRepository.findAllByTradeIdAndOwner(tradeId, owner)).willReturn(rows);

    // when
    List<Long> result = tradeItemService.readTradeItemIdsProcess(tradeId, owner);

    // then
    assertThat(result).containsExactlyInAnyOrder(
        DEFAULT_TRADE_SELLER_ITEM_1.getItemId(),
        DEFAULT_TRADE_SELLER_ITEM_2.getItemId()
    );
  }

  @Test
  void 소유자별_거래_물품_조회() {
    // given
    Long tradeId = 7L;
    given(tradeItemRepository.findAllByTradeIdAndOwner(tradeId, SELLER)).willReturn(List.of(DEFAULT_TRADE_SELLER_ITEM_1, DEFAULT_TRADE_SELLER_ITEM_2));
    given(tradeItemRepository.findAllByTradeIdAndOwner(tradeId, BUYER)).willReturn(List.of(DEFAULT_TRADE_BUYER_ITEM_1, DEFAULT_TRADE_BUYER_ITEM_2));

    // when
    Map<Owner, List<Long>> map = tradeItemService.readTradeItemsOwnerMapProcess(tradeId);

    // then
    assertThat(map.get(SELLER)).containsExactlyInAnyOrder(
        DEFAULT_TRADE_SELLER_ITEM_1.getItemId(),
        DEFAULT_TRADE_SELLER_ITEM_2.getItemId()
    );
    assertThat(map.get(BUYER)).containsExactlyInAnyOrder(
        DEFAULT_TRADE_BUYER_ITEM_1.getItemId(),
        DEFAULT_TRADE_BUYER_ITEM_2.getItemId()
    );
  }

  @Test
  void 거래_물품_변경_추가_삭제() {
    // given
    Long tradeId = 1L;
    Owner owner = BUYER;
    // 현재: [1,2,3]  요청: [2,3,4,5]
    // => [1] 삭제 / [4,5] 추가
    given(tradeItemRepository.findAllItemId(tradeId, owner)).willReturn(List.of(1L, 2L, 3L));
    ChangeTradeItemsCommand command = new ChangeTradeItemsCommand(tradeId, List.of(2L, 3L, 4L, 5L), OTHER_MEMBER_ID);

    // when
    tradeItemService.changeTradeItemsProcess(command, 1L, owner);

    // then
    // 삭제
    ArgumentCaptor<List<Long>> deleteCaptor = ArgumentCaptor.forClass(List.class);
    then(tradeItemRepository).should().deleteTradeItem(eq(tradeId), eq(owner), deleteCaptor.capture());
    assertThat(deleteCaptor.getValue()).containsExactly(1L);

    // 추가
    ArgumentCaptor<List<TradeItem>> saveCaptor = ArgumentCaptor.forClass(List.class);
    then(tradeItemRepository).should().saveAll(saveCaptor.capture());

    List<TradeItem> toSave = saveCaptor.getValue();
    assertThat(toSave).hasSize(2);
    assertThat(toSave).extracting(TradeItem::getItemId).containsExactlyInAnyOrder(4L, 5L);
    assertThat(toSave).allSatisfy(item -> {
      assertThat(item.getTradeId()).isEqualTo(tradeId);
      assertThat(item.getOwner()).isEqualTo(owner);
    });

    then(memberPort).should().changeMemberBookUsage(OTHER_MEMBER_ID, 1L, List.of(1L), true);
    then(memberPort).should().changeMemberBookUsage(OTHER_MEMBER_ID, 1L, List.of(4L, 5L), false);
  }
}

package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.TradeItem;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.entity.type.Owner;
import com.bob.domain.trade.repository.TradeItemRepository;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.support.TestContainerSupport;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("거래 물품 서비스 통합 테스트")
@Transactional
@SpringBootTest
public class TradeItemServiceIntgTest extends TestContainerSupport {

  @Autowired
  TradeItemService tradeItemService;

  @Autowired
  TradeItemRepository tradeItemRepository;

  @Autowired
  TradeRepository tradeRepository;

  @MockitoBean
  TradeMemberPort memberPort;

  private Long tradeId;
  private Long postId = 1L;
  private UUID sellerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
  private UUID buyerId = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca1");

  @BeforeEach
  void setUp() {
    tradeId = tradeRepository.save(createTrade(buyerId)).getId();
  }

  @AfterEach
  void tearDown() {
    tradeItemRepository.deleteAll();
    tradeRepository.deleteAll();
  }

  @Test
  void 거래_물품_생성() {
    // given
    List<Long> itemIds = List.of(1L, 2L, 3L);
    CreateTradeItemsCommand command = CreateTradeItemsCommand.of(tradeId, itemIds, SELLER);

    // when
    tradeItemService.createTradeItemsProcess(command);

    // then
    List<Long> saved = tradeItemRepository.findAllItemId(tradeId, SELLER);
    assertThat(saved).containsExactlyInAnyOrder(1L, 2L, 3L);
  }

  @Test
  void 거래_물품_ITEM_ID_목록_조회() {
    // given
    saveItems(tradeId, SELLER, List.of(1L, 2L));
    saveItems(tradeId, BUYER, List.of(3L, 4L));

    // when
    List<Long> sellerItems = tradeItemService.readTradeItemIdsProcess(tradeId, SELLER);
    List<Long> buyerItems = tradeItemService.readTradeItemIdsProcess(tradeId, BUYER);

    // then
    assertThat(sellerItems).containsExactlyInAnyOrder(1L, 2L);
    assertThat(buyerItems).containsExactlyInAnyOrder(3L, 4L);
  }

  @Test
  void 거래_물품_소유자_별_ITEM_ID_MAP_조회() {
    // given
    saveItems(tradeId, SELLER, List.of(1L, 2L, 3L));
    saveItems(tradeId, BUYER, List.of(4L, 5L));

    // when
    Map<Owner, List<Long>> map = tradeItemService.readTradeItemsOwnerMapProcess(tradeId);

    // then
    assertThat(map.get(SELLER)).containsExactlyInAnyOrder(1L, 2L, 3L);
    assertThat(map.get(BUYER)).containsExactlyInAnyOrder(4L, 5L);
  }

  @Test
  void 거래_물품_변경() {
    // given
    saveItems(tradeId, BUYER, List.of(2L, 3L));
    ChangeTradeItemsCommand command = new ChangeTradeItemsCommand(tradeId, List.of(3L, 4L, 5L), buyerId);

    // when
    tradeItemService.changeTradeItemsProcess(command, postId, BUYER);

    // then
    List<Long> after = tradeItemRepository.findAllItemId(tradeId, BUYER);
    assertThat(after).containsExactlyInAnyOrder(3L, 4L, 5L); // [2] 삭제, [4, 5] 추가
  }

  private void saveItems(Long tradeId, Owner owner, List<Long> itemIds) {
    List<TradeItem> items = itemIds.stream()
        .map(id -> TradeItem.create(tradeId, id, owner))
        .toList();
    tradeItemRepository.saveAll(items);
  }

  private Trade createTrade(UUID buyerId) {
    return Trade.builder()
        .postId(postId)
        .sellerId(sellerId)
        .buyerId(buyerId)
        .status(Status.REQUESTED)
        .updatedAt(now())
        .build();
  }
}
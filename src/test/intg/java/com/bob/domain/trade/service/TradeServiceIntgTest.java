package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.Status.CANCELED;
import static com.bob.domain.trade.entity.status.Status.COMPLETED;
import static com.bob.domain.trade.entity.status.Status.REJECTED;
import static com.bob.domain.trade.entity.status.Status.REQUESTED;
import static com.bob.domain.trade.entity.status.Status.RESERVED;
import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;
import static com.bob.support.fixture.query.TradeQueryFixture.KEY_NULL_STATUS_NULL;
import static com.bob.support.fixture.query.TradeQueryFixture.RECEIVED_STATUS_REQUESTED;
import static com.bob.support.fixture.query.TradeQueryFixture.SENT_STATUS_REQUESTED_REJECTED;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.TestContainerSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("거래 서비스 통합 테스트")
@Transactional
@SpringBootTest
class TradeServiceIntgTest extends TestContainerSupport {

  @Autowired
  private TradeService tradeService;

  @Autowired
  private TradeRepository tradeRepository;

  @Autowired
  private TradeItemService tradeItemService;

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  @MockitoBean
  private ChatRoomReader chatRoomReader;

  private Trade testTrade;

  private ChatRoom chatRoom;

  private Long postId = 1L;
  private UUID sellerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
  private UUID buyerId1 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca1");
  private UUID buyerId2 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca2");
  private UUID buyerId3 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca3");

  private Pageable pageable = PageRequest.of(0, 12);

  @BeforeEach
  void setUp() {
    testTrade = tradeRepository.save(createTrade(postId, buyerId1, REQUESTED));
    insertTradeItem(testTrade.getId(), List.of(1L), List.of(2L, 3L));

    Trade trade2 = tradeRepository.save(createTrade(postId, buyerId2, RESERVED));
    insertTradeItem(trade2.getId(), List.of(1L), List.of(6L));

    Trade trade3 = tradeRepository.save(createTrade(postId, buyerId3, REQUESTED));
    insertTradeItem(trade3.getId(), List.of(1L), List.of(7L));

    chatRoom = createChatRoom(testTrade);
    chatRoomRepository.save(chatRoom);
  }

  @AfterEach
  void tearDown() {
    tradeRepository.deleteAll();
    chatRoomRepository.deleteAll();
  }

  @Test
  void 거래_생성() {
    // given
    Long postId = 2L;
    List<Long> requestItemIds = List.of(2L, 3L);
    CreateTradeCommand command = CreateTradeCommand.of(postId, buyerId1, requestItemIds, false);

    // when
    CreateTradeResponse response = tradeService.createTradeProcess(command);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isNotNull();

    Trade saved = tradeRepository.findById(response.id()).orElseThrow();
    assertThat(saved.getPostId()).isEqualTo(postId);
    assertThat(saved.getSellerId()).isEqualTo(sellerId);
    assertThat(saved.getBuyerId()).isEqualTo(buyerId1);
    assertThat(saved.getStatus()).isEqualTo(REQUESTED);

    List<Long> itemIds = tradeItemService.readTradeItemIdsProcess(saved.getId(), BUYER);
    assertThat(itemIds).containsExactlyInAnyOrder(2L, 3L);
  }

  @Test
  void 게시글_거래_목록_조회() {
    // given
    ReadPostTradesQuery query = new ReadPostTradesQuery(postId, sellerId);

    // when
    PostTradesResponse response = tradeService.readPostTradesProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(3);
  }

  @Test
  void 게시글_거래_목록_조회_시_게시글_등록자가_아니면_예외가_발생한다() {
    // given
    UUID otherUser = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");
    ReadPostTradesQuery query = new ReadPostTradesQuery(postId, otherUser);

    // when & then
    assertThatThrownBy(() -> tradeService.readPostTradesProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  @Test
  void 거래_목록_조회_시_key_null_status_null_이면_모든_거래_응답() {
    // given: sellerId 기준으로 ALL + 상태필터 미적용 → seller의 모든 거래 조회
    Trade trade = tradeRepository.save(createTrade(2L, buyerId1, COMPLETED)); // 거래 추가 1건(완료 상태)
    insertTradeItem(trade.getId(), List.of(1L), List.of(5L));

    ReadTradesQuery query = KEY_NULL_STATUS_NULL(sellerId);

    // when
    TradesResponse response = tradeService.readTradesProcess(query, pageable);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(4); // setUp: 거래 3건 + 신규 1건(완료)
  }

  @Test
  void 거래_목록_조회_시_key_SENT_status_REQUESTED_REJECTED_사용자가_보낸_제안_거절_상태의_거래_응답() {
    // given: buyer1 기준 SENT + [REQUESTED, REJECTED] → buyer가 보낸 제안, 거절 상태의 거래 2건
    Trade trade1 = tradeRepository.save(createTrade(2L, buyerId1, REJECTED));// 추가 1건(거절 상태)
    insertTradeItem(trade1.getId(), List.of(1L), List.of(3L));
    Trade trade2 = tradeRepository.save(createTrade(3L, buyerId1, COMPLETED));// 추가 1건(완료 상태) <- 응답 반영 X
    insertTradeItem(trade2.getId(), List.of(1L), List.of(4L));
    ReadTradesQuery query = SENT_STATUS_REQUESTED_REJECTED(buyerId1);

    // when
    TradesResponse response = tradeService.readTradesProcess(query, pageable);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(2); // setUp: 거래 1건(제안) + 신규 1건(거절)
    assertThat(response.trades().stream().map(TradeSummary::status).toList())
        .containsOnly("REQUESTED", "REJECTED");
  }

  @Test
  void 거래_목록_조회_시_key_RECEIVED_status_REQUESTED_이면_사용자가_받은_제안_상태_거래_응답() {
    // given: seller 기준 RECEIVED + [REQUESTED] → seller가 받은 제안 상태의 거래 2건(by buyer1, by buyer3)
    Trade trade = tradeRepository.save(createTrade(18L, sellerId, REJECTED)); // 추가 1건(거절 상태) <- 응답 반영 X
    insertTradeItem(trade.getId(), List.of(8L), List.of(1L));
    ReadTradesQuery query = RECEIVED_STATUS_REQUESTED(sellerId);

    // when
    TradesResponse response = tradeService.readTradesProcess(query, pageable);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(2); // setUp: 거래 2건(제안)
    assertThat(response.trades().stream().map(TradeSummary::status).toList())
        .containsOnly("REQUESTED");
  }

  @Test
  void 거래_상세_조회() {
    // given
    ReadTradeDetailQuery query = new ReadTradeDetailQuery(testTrade.getId(), sellerId);

    // when
    TradeDetailResponse response = tradeService.readTradeDetailProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(testTrade.getId());
    assertThat(response.status()).isEqualTo(testTrade.getStatus().name());

    assertThat(response.seller()).isNotNull();
    assertThat(response.seller().id()).isEqualTo(sellerId);
    assertThat(response.seller().item()).hasSize(1);
    assertThat(response.seller().worth()).isEqualTo(15000);

    assertThat(response.buyer()).isNotNull();
    assertThat(response.buyer().id()).isEqualTo(buyerId1);
    assertThat(response.buyer().item()).hasSize(2);
    assertThat(response.buyer().worth()).isEqualTo(40000);
  }

  @Test
  void 거래_상세_조회_시_거래_참여자가_아니면_예외가_발생한다() {
    // given
    Trade trade = tradeRepository.findAllByPostId(1L).get(0);
    Long tradeId = trade.getId();
    UUID nonParticipantId = UUID.randomUUID();

    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, nonParticipantId);

    // when & then
    assertThatThrownBy(() -> tradeService.readTradeDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  @Test
  void 거래_상태_변경_수락() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now())); // 예약 상태 제거
    chatRoomRepository.deleteAll(); // 수락 시 채팅방 생성 검증을 위한 채팅방 제거
    Trade trade = testTrade;
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "ACCEPTED", null);

    // when
    ChangeTradeStatusResult result = tradeService.changeTradeStatusProcess(command);

    // then
    Trade updated = tradeRepository.findById(trade.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo(Status.ACCEPTED);
    assertThat(result.chatroomId()).isNotNull();
  }

  @Test
  void 거래_상태_변경_예약() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now())); // 대기 상태로 변경
    Trade trade = testTrade;
    given(chatRoomReader.readExistingChatRoom(postId, sellerId, buyerId1)).willReturn(Optional.of(chatRoom.getId()));
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "RESERVED", null);

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    Trade updatedTrade = tradeRepository.findById(trade.getId()).orElseThrow();
    assertThat(updatedTrade.getStatus()).isEqualTo(RESERVED);
  }

  @Test
  void 거래_상태_변경_완료() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now())); // setup 예약상태 제거
    Trade trade = testTrade;
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "COMPLETED", null);

    // when
    ChangeTradeStatusResult result = tradeService.changeTradeStatusProcess(command);

    // then
    Trade updated = tradeRepository.findById(trade.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo(COMPLETED);
    assertThat(result.chatroomId()).isNull();

    List<Trade> otherTrades = tradeRepository.findAllByPostId(postId);
    otherTrades.stream()
        .filter(t -> !t.getId().equals(trade.getId()))
        .forEach(t -> assertThat(t.getStatus()).isEqualTo(CANCELED));
  }

  @Test
  void 거래_상태_변경_시_이미_처리된_거래가_있으면_예외가_발생한다() {
    // given
    Trade trade = testTrade;
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "RESERVED", null);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ALREADY_PROCESSED.getMessage());
  }

  @Test
  void 거래_상태_변경_시_게시글_소유자가_아니면_예외가_발생한다() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now()));
    Trade trade = testTrade;
    UUID otherUserId = UUID.fromString("0197365f-8074-7d24-a332-999999999999");
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(otherUserId, trade.getId(), "RESERVED", null);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  @Test
  void 거래_물품_변경() {
    // given
    Trade trade = testTrade;
    testTrade.updateTradeStatus(CANCELED, now());
    List<Long> requestIds = List.of(2L, 4L);
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(trade.getId(), requestIds, buyerId1);
    given(chatRoomReader.readExistingChatRoom(postId, buyerId1, sellerId)).willReturn(Optional.of(chatRoom.getId()));

    // 사전 검증
    assertThat(trade.getStatus()).isEqualTo(CANCELED);
    List<Long> currentItemIds = tradeItemService.readTradeItemIdsProcess(testTrade.getId(), BUYER);
    assertThat(currentItemIds).containsExactlyInAnyOrder(2L, 3L);

    // when
    tradeService.changeTradeItemProcess(command);

    // then
    assertThat(trade.getStatus()).isEqualTo(REQUESTED);
    List<Long> savedItemIds = tradeItemService.readTradeItemIdsProcess(testTrade.getId(), BUYER);
    assertThat(savedItemIds).containsExactlyInAnyOrder(2L, 4L)
        .doesNotContain(3L);
  }

  private Trade createTrade(Long postId, UUID buyerId, Status status) {
    return Trade.builder()
        .postId(postId)
        .sellerId(sellerId)
        .buyerId(buyerId)
        .status(status)
        .updatedAt(now())
        .build();
  }

  private void insertTradeItem(Long tradeId, List<Long> sellerItemIds, List<Long> buyerItemIds) {
    tradeItemService.createTradeItemsProcess(CreateTradeItemsCommand.of(tradeId, sellerItemIds, SELLER));
    tradeItemService.createTradeItemsProcess(CreateTradeItemsCommand.of(tradeId, buyerItemIds, BUYER));
  }

  private ChatRoom createChatRoom(Trade trade) {
    return ChatRoom.builder()
        .postId(trade.getPostId())
        .tradeId(trade.getId())
        .titleSuffix("test")
        .build();
  }
}

package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.TradeStatus.REQUESTED;
import static com.bob.domain.trade.entity.status.TradeStatus.RESERVED;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.CUSTOM_MEMBER_PROFILE_RESPONSE;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.TradeStatus;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.support.TestContainerSupport;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
  private TradeReader tradeReader;

  @Autowired
  private ChatRoomRepository chatRoomRepository;

  @MockitoBean
  private TradeMemberPort memberPort;

  @MockitoBean
  private TradePostPort postPort;

  @MockitoBean
  private ChatRoomReader chatRoomReader;

  private Trade testTrade;

  private ChatRoom chatRoom;

  private Long postId = 1L;
  private UUID sellerId = UUID.fromString("0197365f-8074-7d24-a332-95c9ebd1f5c0");
  private UUID buyerId1 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca1");
  private UUID buyerId2 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca2");
  private UUID buyerId3 = UUID.fromString("0197365f-8074-7d24-ba91-0c5fc1b37ca3");

  @BeforeEach
  void setUp() {
    testTrade = tradeRepository.save(createTrade(buyerId1, REQUESTED));
    tradeRepository.save(createTrade(buyerId2, RESERVED));
    tradeRepository.save(createTrade(buyerId3, REQUESTED));
    chatRoom = createChatRoom(testTrade);
    chatRoomRepository.save(chatRoom);
  }

  @AfterEach
  void tearDown() {
    tradeRepository.deleteAll();
    chatRoomRepository.deleteAll();
  }

  @Test
  @DisplayName("거래 목록 조회 - 성공 테스트")
  void 판매자는_거래_목록을_정상_조회할_수_있다() {
    // given
    ReadTradesQuery query = new ReadTradesQuery(postId, sellerId);

    given(postPort.readTradePostOwnerId(postId)).willReturn(sellerId);
    given(memberPort.readTradeMemberProfile(any(UUID.class)))
        .willAnswer(invocation -> CUSTOM_MEMBER_PROFILE_RESPONSE(invocation.getArgument(0)));

    // when
    TradesResponse response = tradeService.readTradesProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(3);
  }

  @Test
  @DisplayName("거래 목록 조회 - 실패 테스트 (판매자 X)")
  void 판매자가_아니면_거래_목록_조회_시_예외가_발생한다() {
    // given
    UUID otherUser = UUID.fromString("0197365f-8074-7d24-a332-0c5f1dbe9c59");
    ReadTradesQuery query = new ReadTradesQuery(postId, otherUser);

    given(postPort.readTradePostOwnerId(postId)).willReturn(sellerId);

    // when & then
    assertThatThrownBy(() -> tradeService.readTradesProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  @Test
  @DisplayName("거래 상세 조회 - 성공 테스트")
  void 거래_상세조회_시_판매자인_경우_성공한다() {
    // given
    Trade trade = tradeRepository.findAllByPostId(1L).get(0);
    UUID sellerId = trade.getSellerId();
    Long tradeId = trade.getId();

    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, sellerId);

    // when
    TradeDetailResponse response = tradeService.readTradeDetailProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(tradeId);
    assertThat(response.sellerId()).isEqualTo(sellerId);
  }

  @Test
  @DisplayName("거래 상세 조회 - 실패 테스트 (거래 참여자 X)")
  void 거래_상세조회_비참여자라면_예외발생() {
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
  @DisplayName("거래 상태 변경 - 성공 테스트")
  void 거래_상태를_정상적으로_변경할_수_있다() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now())); // 대기 상태로 변경
    Trade trade = testTrade;
    given(postPort.readTradePostOwnerId(trade.getPostId())).willReturn(sellerId);
    given(chatRoomReader.readExistingChatRoom(postId, sellerId, buyerId1)).willReturn(Optional.of(chatRoom.getId()));
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "RESERVED");

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    Trade updatedTrade = tradeRepository.findById(trade.getId()).orElseThrow();
    assertThat(updatedTrade.getTradeStatus()).isEqualTo(RESERVED);
  }

  @Test
  @DisplayName("거래 상태 변경 - 실패 테스트 (이미 처리된 거래 존재)")
  void 거래_상태_변경시_이미_처리된_거래가_있으면_예외가_발생한다() {
    // given
    Trade trade = tradeRepository.findAllByPostId(postId).get(0);
    given(postPort.readTradePostOwnerId(trade.getPostId())).willReturn(sellerId);
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "RESERVED");

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ALREADY_PROCESSED.getMessage());
  }

  @Test
  @DisplayName("거래 상태 변경 - 실패 테스트 (게시글 소유자 아님)")
  void 거래_상태_변경시_게시글_소유자가_아니면_예외가_발생한다() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now()));
    Trade trade = tradeRepository.findAllByPostId(postId).get(0);
    UUID otherUserId = UUID.fromString("0197365f-8074-7d24-a332-999999999999");
    given(postPort.readTradePostOwnerId(trade.getPostId())).willReturn(sellerId);
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(otherUserId, trade.getId(), "RESERVED");

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  private Trade createTrade(UUID buyerId, TradeStatus status) {
    return Trade.builder()
        .postId(postId)
        .sellerId(sellerId)
        .buyerId(buyerId)
        .tradeStatus(status)
        .updatedAt(now())
        .build();
  }

  private ChatRoom createChatRoom(Trade trade) {
    return ChatRoom.builder()
        .postId(trade.getPostId())
        .tradeId(trade.getId())
        .titleSuffix("test")
        .build();
  }
}

package com.bob.domain.trade.service;

import static com.bob.domain.chat.entity.status.TradeStatus.ACCEPTED;
import static com.bob.domain.trade.entity.status.Status.COMPLETED;
import static com.bob.domain.trade.entity.status.Status.REJECTED;
import static com.bob.domain.trade.entity.status.Status.REQUESTED;
import static com.bob.domain.trade.entity.status.Status.RESERVED;
import static com.bob.support.fixture.query.TradeQueryFixture.KEY_NULL_STATUS_NULL;
import static com.bob.support.fixture.query.TradeQueryFixture.RECEIVED_STATUS_REQUESTED;
import static com.bob.support.fixture.query.TradeQueryFixture.SENT_STATUS_REQUESTED_REJECTED;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.CUSTOM_MEMBER_PROFILE_RESPONSE;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.domain.chat.service.reader.ChatRoomReader;
import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.post.service.dto.response.PostDetailResponse.BookInfo;
import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.dto.response.internal.TradeSummary;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
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

  private Pageable pageable = PageRequest.of(0, 12);

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
  void 거래_생성() {
    // given
    Long targetPostId = postId;
    List<Long> exchangeBookIds = List.of(10L, 11L);
    given(postPort.readTradePostSummary(targetPostId)).willReturn(mockPostResponse());
    given(memberPort.readTradeMemberProfile(any(UUID.class)))
        .willAnswer(invocation -> CUSTOM_MEMBER_PROFILE_RESPONSE(invocation.getArgument(0)));
    CreateTradeCommand command = CreateTradeCommand.of(targetPostId, buyerId1, exchangeBookIds);

    // when
    CreateTradeResponse response = tradeService.createTradeProcess(command);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isNotNull();

    Trade saved = tradeRepository.findById(response.id()).orElseThrow();
    assertThat(saved.getPostId()).isEqualTo(targetPostId);
    assertThat(saved.getSellerId()).isEqualTo(sellerId);
    assertThat(saved.getBuyerId()).isEqualTo(buyerId1);
    assertThat(saved.getStatus()).isEqualTo(REQUESTED);

    then(memberPort).should().changeMemberBookUsage(targetPostId, exchangeBookIds, false);
  }

  @Test
  void 게시글_거래_목록_조회() {
    // given
    ReadPostTradesQuery query = new ReadPostTradesQuery(postId, sellerId);
    given(postPort.readTradePostSummary(postId)).willReturn(mockPostResponse());
    given(memberPort.readTradeMemberProfile(any(UUID.class)))
        .willAnswer(invocation -> CUSTOM_MEMBER_PROFILE_RESPONSE(invocation.getArgument(0)));

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
    given(postPort.readTradePostSummary(postId)).willReturn(mockPostResponse());

    // when & then
    assertThatThrownBy(() -> tradeService.readPostTradesProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  @Test
  void 거래_목록_조회_시_key_null_status_null_이면_모든_거래_응답() {
    // given: sellerId 기준으로 ALL + 상태필터 미적용 → seller의 모든 거래 조회
    tradeRepository.save(createTrade(buyerId1, COMPLETED)); // 추가 1건(완료 상태)
    ReadTradesQuery query = KEY_NULL_STATUS_NULL(sellerId);
    given(postPort.readTradePostSummary(any(Long.class))).willReturn(mockPostResponse());

    // when
    TradesResponse response = tradeService.readTradesProcess(query, pageable);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(4); // setUp: 거래 3건 + 신규 1건(완료)
  }

  @Test
  void 거래_목록_조회_시_key_SENT_status_REQUESTED_REJECTED_사용자가_보낸_제안_거절_상태의_거래_응답() {
    // given: buyer1 기준 SENT + [REQUESTED, REJECTED] → buyer가 보낸 제안, 거절 상태의 거래 2건
    tradeRepository.save(createTrade(buyerId1, REJECTED)); // 추가 1건(거절 상태)
    tradeRepository.save(createTrade(buyerId1, COMPLETED)); // 추가 1건(완료 상태) <- 응답 반영 X
    ReadTradesQuery query = SENT_STATUS_REQUESTED_REJECTED(buyerId1);
    given(postPort.readTradePostSummary(any(Long.class))).willReturn(mockPostResponse());

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
    tradeRepository.save(createTrade(sellerId, REJECTED)); // 추가 1건(거절 상태) <- 응답 반영 X
    ReadTradesQuery query = RECEIVED_STATUS_REQUESTED(sellerId);
    given(postPort.readTradePostSummary(any(Long.class))).willReturn(mockPostResponse());

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
  void 거래_상태_변경() {
    // given
    tradeRepository.findAllByPostId(postId).forEach(t -> t.updateTradeStatus(REQUESTED, now())); // 대기 상태로 변경
    Trade trade = testTrade;
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(mockPostResponse());
    given(chatRoomReader.readExistingChatRoom(postId, sellerId, buyerId1)).willReturn(Optional.of(chatRoom.getId()));
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(sellerId, trade.getId(), "RESERVED", null);

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    Trade updatedTrade = tradeRepository.findById(trade.getId()).orElseThrow();
    assertThat(updatedTrade.getStatus()).isEqualTo(RESERVED);
  }

  @Test
  void 거래_상태_변경_시_이미_처리된_거래가_있으면_예외가_발생한다() {
    // given
    Trade trade = testTrade;
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(mockPostResponse());
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
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(mockPostResponse());
    ChangeTradeStatusCommand command = new ChangeTradeStatusCommand(otherUserId, trade.getId(), "RESERVED", null);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.TRADE_ACCESS_DENIED.getMessage());
  }

  private Trade createTrade(UUID buyerId, Status status) {
    return Trade.builder()
        .postId(postId)
        .sellerId(sellerId)
        .buyerId(buyerId)
        .status(status)
        .updatedAt(now())
        .build();
  }

  private ChatRoom createChatRoom(Trade trade) {
    return ChatRoom.builder()
        .postId(trade.getPostId())
        .tradeId(trade.getId())
        .titleSuffix("test")
        .tradeStatus(ACCEPTED)
        .build();
  }

  private PostDetailResponse mockPostResponse() {
    return PostDetailResponse.builder()
        .postId(postId)
        .sellerId(sellerId)
        .book(BookInfo.builder().title("자바의 정석").build())
        .build();
  }
}

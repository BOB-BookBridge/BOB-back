package com.bob.domain.trade.service;

import static com.bob.domain.trade.entity.status.Status.REQUESTED;
import static com.bob.domain.trade.entity.type.Owner.BUYER;
import static com.bob.domain.trade.entity.type.Owner.SELLER;
import static com.bob.global.exception.response.ApplicationError.IS_SAME_TRADE_MEMBER;
import static com.bob.global.exception.response.ApplicationError.MAIN_TRADE_ITEM_CONTAINED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_ABORTED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_COMPLETED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.global.exception.response.ApplicationError.TRADE_POST_REMOVED;
import static com.bob.global.exception.response.ApplicationError.TRADE_STATUS_NOT_CHANGEABLE;
import static com.bob.global.exception.response.ApplicationError.UNCHANGEABLE_TRADE_ITEM;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND_WITH_REASON;
import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.command.CreateTradeCommandFixture.SAME_MEMBER_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.MemberFixture.OTHER_MEMBER_ID;
import static com.bob.support.fixture.domain.TradeFixture.ACCEPTED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.CANCELED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.COMPLETED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADES;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADE_WITH_ID;
import static com.bob.support.fixture.domain.TradeFixture.REQUESTED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.RESERVED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.TRADE;
import static com.bob.support.fixture.query.TradeQueryFixture.KEY_NULL_STATUS_NULL;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.CUSTOM_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.CUSTOM_POST_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.trade.internal.TradeItemViewFixture.ALL_TRADE_ITEM_VIEWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.post.service.dto.response.PostDetailResponse;
import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.entity.status.Status;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeItemsCommand;
import com.bob.domain.trade.service.dto.query.ReadParticipateTradeStatusQuery;
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.query.SearchKey;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradeStatusMapResult;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.port.out.TradeChatPort;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("거래 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

  @InjectMocks
  private TradeService tradeService;

  @Mock
  private TradeRepository tradeRepository;

  @Mock
  private TradeReader tradeReader;

  @Mock
  private TradeItemService tradeItemService;

  @Mock
  private TradeMemberPort memberPort;

  @Mock
  private TradePostPort postPort;

  @Mock
  private TradeChatPort chatPort;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  private Pageable pageable = PageRequest.of(0, 12);

  @Test
  void 거래_생성_및_저장() {
    // given
    CreateTradeCommand command = DEFAULT_CREATE_TRADE_COMMAND;
    given(postPort.readTradePostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeRepository.findIdByPostIdAndBuyerId(command.postId(), command.buyerId())).willReturn(Optional.empty());
    given(tradeRepository.save(any(Trade.class))).willReturn(DEFAULT_TRADE_WITH_ID);
    given(memberPort.readTradeMemberProfile(command.buyerId())).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    CreateTradeResponse response = tradeService.createTradeProcess(command);

    // then
    assertThat(response.id()).isEqualTo(1L);
    then(tradeRepository).should(times(1)).findIdByPostIdAndBuyerId(anyLong(), any(UUID.class));
    then(tradeRepository).should(times(1)).save(any(Trade.class));
    then(memberPort).should(times(1)).changeMemberBookUsage(OTHER_MEMBER_ID, command.postId(), command.itemIds(), false);
    then(tradeItemService).should(times(2)).createTradeItemsProcess(any(CreateTradeItemsCommand.class));
  }

  @Test
  void 거래_생성_및_저장_시_기존_거래가_존재하면_해당_거래의_id_반환() {
    // given
    CreateTradeCommand command = DEFAULT_CREATE_TRADE_COMMAND;
    given(postPort.readTradePostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeRepository.findIdByPostIdAndBuyerId(command.postId(), command.buyerId())).willReturn(Optional.of(1L));

    // when
    CreateTradeResponse response = tradeService.createTradeProcess(command);

    // then
    assertThat(response.id()).isEqualTo(1L);
    then(tradeRepository).should(times(1)).findIdByPostIdAndBuyerId(anyLong(), any(UUID.class));
    then(tradeRepository).shouldHaveNoMoreInteractions();
    then(memberPort).shouldHaveNoInteractions();
    then(tradeItemService).shouldHaveNoInteractions();
  }

  @Test
  void 거래_생성_및_저장_시_자기_자신과의_교환을_요청하면_예외가_발생한다() {
    // given
    CreateTradeCommand command = SAME_MEMBER_CREATE_TRADE_COMMAND;
    given(postPort.readTradePostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.createTradeProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(IS_SAME_TRADE_MEMBER.getMessage());
  }

  @Test
  void 게시글_거래_목록_조회() {
    // given
    UUID requesterId = MEMBER_ID;
    Long postId = 1L;
    ReadPostTradesQuery query = new ReadPostTradesQuery(postId, requesterId);
    given(postPort.readTradePostSummary(postId)).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByPostId(postId)).willReturn(DEFAULT_TRADES());
    given(memberPort.readTradeMemberProfile(any(UUID.class))).willAnswer(invocation -> CUSTOM_MEMBER_PROFILE_RESPONSE(invocation.getArgument(0)));

    // when
    PostTradesResponse response = tradeService.readPostTradesProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(3);
    then(postPort).should(times(1)).readTradePostSummary(postId);
    then(tradeReader).should(times(1)).readTradesByPostId(postId);
    then(memberPort).should(times(3)).readTradeMemberProfile(any(UUID.class));
  }

  @Test
  void 게시글_거래_목록_조회_시_게시글_등록자가_아니면_예외가_발생한다() {
    // given
    UUID postOwnerId = MEMBER_ID;
    UUID requesterId = UUID.randomUUID();
    Long postId = 1L;
    ReadPostTradesQuery query = new ReadPostTradesQuery(postId, requesterId);
    given(postPort.readTradePostSummary(postId)).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.readPostTradesProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(TRADE_ACCESS_DENIED.getMessage());

    then(postPort).should(times(1)).readTradePostSummary(postId);
    then(tradeReader).shouldHaveNoInteractions();
    then(memberPort).shouldHaveNoInteractions();
  }

  @Test
  void 거래_목록_조회() {
    // given
    ReadTradesQuery query = KEY_NULL_STATUS_NULL(MEMBER_ID);
    List<Trade> trades = DEFAULT_TRADES();
    given(postPort.readTradePostSummary(1L)).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByQuery(any(ReadTradesQuery.class), eq(pageable))).willReturn(trades);
    given(tradeRepository.countTradesByQuery(any(ReadTradesQuery.class))).willReturn((long) trades.size());

    // when
    TradesResponse response = tradeService.readTradesProcess(query, pageable);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(trades.size());

    ArgumentCaptor<ReadTradesQuery> captor = ArgumentCaptor.forClass(ReadTradesQuery.class);
    then(tradeReader).should(times(1)).readTradesByQuery(captor.capture(), eq(pageable));
    then(tradeRepository).should(times(1)).countTradesByQuery(any(ReadTradesQuery.class));

    ReadTradesQuery captured = captor.getValue();
    assertThat(captured.memberId()).isEqualTo(query.memberId());
    assertThat(captured.key()).isEqualTo(SearchKey.ALL);
    assertThat(captured.statuses()).isNull();
  }

  @Test
  void 거래_상세_조회() {
    // given
    Long tradeId = 1L;
    Trade trade = DEFAULT_TRADE_WITH_ID;
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);

    // 거래 아이템 ID 맵 (판매자 1건, 구매자 2건)
    List<Long> sellerItemIds = List.of(1L);
    List<Long> buyerItemIds  = List.of(2L, 3L);
    given(tradeItemService.readTradeItemsOwnerMapProcess(tradeId)).willReturn(Map.of(SELLER, sellerItemIds, BUYER, buyerItemIds));

    // 아이템 상세 응답 (가격: 판매자 10000, 구매자 6000 + 8000)
    List<Long> allIds = List.of(1L, 2L, 3L);
    given(memberPort.readTradeItemSummary(allIds)).willReturn(ALL_TRADE_ITEM_VIEWS);

    // 게시글/회원 요약
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(memberPort.readTradeMemberProfile(trade.getSellerId())).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);
    given(memberPort.readTradeMemberProfile(trade.getBuyerId())).willReturn(CUSTOM_MEMBER_PROFILE_RESPONSE(trade.getBuyerId()));

    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, trade.getSellerId());

    // when
    TradeDetailResponse response = tradeService.readTradeDetailProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(tradeId);
    assertThat(response.status()).isEqualTo(trade.getStatus().name());

    // post
    assertThat(response.post()).isNotNull();
    assertThat(response.post().id()).isEqualTo(trade.getPostId());

    // seller
    assertThat(response.seller()).isNotNull();
    assertThat(response.seller().id()).isEqualTo(trade.getSellerId());
    assertThat(response.seller().worth()).isEqualTo(10000);
    assertThat(response.seller().item()).hasSize(1);
    assertThat(response.seller().item().get(0).id()).isEqualTo(1L);

    // buyer
    assertThat(response.buyer()).isNotNull();
    assertThat(response.buyer().id()).isEqualTo(trade.getBuyerId());
    assertThat(response.buyer().worth()).isEqualTo(14000);
    assertThat(response.buyer().item()).hasSize(2);
    assertThat(response.buyer().item()).extracting("id").containsExactlyInAnyOrder(2L, 3L);

    then(tradeReader).should().readTradeById(tradeId);
    then(tradeItemService).should().readTradeItemsOwnerMapProcess(tradeId);
    then(memberPort).should().readTradeItemSummary(allIds);
    then(postPort).should().readTradePostSummary(trade.getPostId());
    then(memberPort).should().readTradeMemberProfile(trade.getSellerId());
    then(memberPort).should().readTradeMemberProfile(trade.getBuyerId());
  }

  @Test
  void 거래_상세_조회_시_거래_참여자가_아니면_예외가_발생한다() {
    // given
    Trade trade = DEFAULT_TRADE_WITH_ID;
    UUID nonParticipantId = UUID.randomUUID();
    Long tradeId = trade.getId();
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, nonParticipantId);

    // when & then
    assertThatThrownBy(() -> tradeService.readTradeDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ACCESS_DENIED.getMessage());

    then(tradeReader).should(times(1)).readTradeById(tradeId);
    then(tradeItemService).shouldHaveNoInteractions();
    then(memberPort).shouldHaveNoInteractions();
    then(postPort).shouldHaveNoInteractions();
  }

  @Test
  void 거래_상태_조회() {
    // given
    UUID memberId = MEMBER_ID;
    List<Long> postIds = List.of(10L, 20L);
    List<Trade> trades = List.of(REQUESTED_TRADE(1L, 10L), RESERVED_TRADE(2L, 20L));
    given(tradeReader.readTradesByBuyerIdAndPostId(memberId, postIds)).willReturn(trades);

    ReadParticipateTradeStatusQuery query = ReadParticipateTradeStatusQuery.of(memberId, postIds);

    // when
    TradeStatusMapResult result = tradeService.readTradeStatusProcess(query);

    // then
    assertThat(result).isNotNull();
    assertThat(result.statusMap()).hasSize(2)
        .containsEntry(10L, "REQUESTED")
        .containsEntry(20L, "RESERVED");

    then(tradeReader).should().readTradesByBuyerIdAndPostId(memberId, postIds);
  }

  @Test
  void 거래_상태_변경() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("ACCEPTED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);
    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(chatPort.create(requestTrade.getId(), requestTrade.getPostId(), requestTrade.getBuyerId(), requestTrade.isFar())).willReturn(1L);

    // when
    ChangeTradeStatusResult result = tradeService.changeTradeStatusProcess(command);

    // then
    assertThat(result.chatroomId()).isEqualTo(1L);
    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
  }

  @Test
  void 거래_상태_변경_취소_사유_포함() {
    // given
    String reason = "cancel reason";
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND_WITH_REASON("CANCELED", reason);

    Trade requestTrade = RESERVED_TRADE(1L, 1L);
    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    final ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    then(postPort).should().changeTradeProgress(requestTrade.getPostId(), "READY");
    then(eventPublisher).should(times(2)).publishEvent(eventCaptor.capture());

    // 채팅, 알림 이벤트 발행 시 body의 거래 취소 사유 포함 여부 검증
    final List<Object> events = eventCaptor.getAllValues();
    assertThat(events).hasSize(2);
    assertThat(events).allMatch(e -> extractEventBody(e).contains(reason));
    then(eventPublisher).shouldHaveNoMoreInteractions();
  }

  @Test
  void 거래_상태_변경_예약에서_이하_단계로_변경하면_게시글_거래_진행_상태_READY로_변경() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("ACCEPTED");
    Trade reserved = RESERVED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(reserved);
    given(postPort.readTradePostSummary(reserved.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when
    ChangeTradeStatusResult result = tradeService.changeTradeStatusProcess(command);

    // then
    then(postPort).should().changeTradeProgress(reserved.getPostId(), "READY");
  }

  @Test
  void 거래_상태_변경_완료시_게시글_연관_타_거래_취소_및_거래_아이템_후처리() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("COMPLETED");
    Trade reservedTrade = RESERVED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(reservedTrade);
    given(postPort.readTradePostSummary(reservedTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByPostId(reservedTrade.getPostId())).willReturn(List.of(reservedTrade));

    // when
    ChangeTradeStatusResult result = tradeService.changeTradeStatusProcess(command);

    // then
    assertThat(result.chatroomId()).isNull();
    then(tradeRepository).should().cancelOtherTrades(reservedTrade.getPostId(), reservedTrade.getId());
    then(tradeItemService).should().freeTraderItemsExcludeMainItem(reservedTrade.getId(), 1L);
    then(tradeItemService).should().removeTraderItems(reservedTrade.getId());
    then(postPort).should().changeTradeProgress(reservedTrade.getId(), "COMPLETED");
    then(chatPort).shouldHaveNoInteractions();
  }

  @Test
  void 거래_상태_변경_시_게시글_소유자가_아니면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(CUSTOM_POST_DETAIL_RESPONSE(1L, UUID.randomUUID(), "REQUESTED"));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ACCESS_DENIED.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
  }

  @Test
  void 거래_상태_변경_시_현재_상태와_요청_상태가_동일하면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = RESERVED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(ApplicationError.TRADE_STATUS_UNCHANGED.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
  }

  @Test
  void 거래_상태_변경_시_이미_완료된_거래라면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("CANCELED");
    Trade completed = COMPLETED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(completed);
    given(postPort.readTradePostSummary(completed.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ALREADY_COMPLETED.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(completed.getPostId());
  }

  @Test
  void 거래_상태_변경_시_요청_상태로_변경을_요청하면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("REQUESTED");
    Trade accepted = ACCEPTED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(accepted);
    given(postPort.readTradePostSummary(accepted.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_STATUS_NOT_CHANGEABLE.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(accepted.getPostId());
  }

  @Test
  void 거래_상태_변경_시_중단된_거래라면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("ACCEPTED");
    Trade canceled = CANCELED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(canceled);
    given(postPort.readTradePostSummary(canceled.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ALREADY_ABORTED.getMessage());
  }

  @Test
  void 거래_상태_변경_시_이미_진행되는_다른_거래가_있다면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);

    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(tradeReader.readTradesByPostId(requestTrade.getPostId())).willReturn(DEFAULT_TRADES()); // 예약된 거래 존재

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ALREADY_PROCESSED.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(tradeReader).should().readTradesByPostId(requestTrade.getPostId());
  }

  @Test
  void 거래_물품_변경() {
    // given
    Long tradeId = 1L;
    List<Long> itemIds = List.of(2L, 3L);
    Trade trade = REQUESTED_TRADE(tradeId, 1L);
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    PostDetailResponse post = DEFAULT_POST_DETAIL_RESPONSE(trade.getPostId());
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(post);
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, itemIds, MEMBER_ID);

    // when
    tradeService.changeTradeItemProcess(command);

    // then
    then(tradeItemService).should().changeTradeItemsProcess(eq(command), eq(post.postId()), eq(SELLER));
  }

  @Test
  void 거래_물품_변경_시_취소_거절_상태가_요청_상태로_변경() {
    // given
    Trade trade = TRADE(1L, 1L, Status.REJECTED);
    List<Long> itemIds = List.of(2L, 3L);
    assertThat(trade.getStatus().isAborted()).isTrue();

    given(tradeReader.readTradeById(trade.getId())).willReturn(trade);
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(trade.getPostId()));
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(1L, itemIds, MEMBER_ID);

    // when
    tradeService.changeTradeItemProcess(command);

    // then
    assertThat(trade.getStatus().isAborted()).isFalse();
    assertThat(trade.getStatus()).isEqualTo(REQUESTED);

    then(tradeItemService).should().changeTradeItemsProcess(eq(command), eq(1L), eq(SELLER));
  }

  @Test
  void 거래_물품_변경_시_거래_진행_상태라면_예외가_발생한다() {
    // given
    Long tradeId = 1L;
    Trade trade = RESERVED_TRADE(tradeId, 1L); // 예약 상태 변경 불가
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(trade.getPostId()));
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, List.of(1L), MEMBER_ID);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeItemProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(UNCHANGEABLE_TRADE_ITEM.getMessage());

    then(tradeItemService).shouldHaveNoInteractions();
    then(eventPublisher).shouldHaveNoInteractions();
  }

  @Test
  void 거래_물품_변경_시_거래_대표_아이템이_포함되면_예외가_발생한다() {
    // given
    Long tradeId = 1L;
    Trade trade = REQUESTED_TRADE(tradeId, 1L);
    List<Long> itemIds = List.of(1L, 2L); // id: 1, 대표 물품
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(trade.getPostId()));
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, itemIds, MEMBER_ID);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeItemProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(MAIN_TRADE_ITEM_CONTAINED.getMessage());

    then(tradeItemService).shouldHaveNoInteractions();
    then(eventPublisher).shouldHaveNoInteractions();
  }

  @Test
  void 거래_물품_변경_시_거래_참여자가_아니면_예외가_발생한다() {
    // given
    Long tradeId = 1L;
    Trade trade = REQUESTED_TRADE(tradeId, 1L);
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(trade.getPostId()));

    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, List.of(), UUID.randomUUID());

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeItemProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ACCESS_DENIED.getMessage());

    then(tradeItemService).shouldHaveNoInteractions();
    then(eventPublisher).shouldHaveNoInteractions();
  }

  @Test
  void 거래_물품_변경_시_게시글이_삭제_된_상태면_예외가_발생한다() {
    // given
    Long tradeId = 1L;
    Trade trade = REQUESTED_TRADE(tradeId, 1L);
    List<Long> itemIds = List.of(2L, 3L);
    given(tradeReader.readTradeById(tradeId)).willReturn(trade);

    given(postPort.readTradePostSummary(trade.getPostId())).willReturn(CUSTOM_POST_DETAIL_RESPONSE(1L, MEMBER_ID, "REMOVED"));
    ChangeTradeItemsCommand command = ChangeTradeItemsCommand.of(tradeId, itemIds, MEMBER_ID);

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeItemProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_POST_REMOVED.getMessage());

    then(tradeItemService).shouldHaveNoInteractions();
    then(eventPublisher).shouldHaveNoInteractions();
  }

  private static String extractEventBody(Object event) {
    try {
      Method m1 = event.getClass().getMethod("getBody");
      Object o1 = m1.invoke(event);
      return String.valueOf(o1);
    } catch (NoSuchMethodException e1) {
      try {
        Method m2 = event.getClass().getMethod("body");
        Object o2 = m2.invoke(event);
        return String.valueOf(o2);
      } catch (Exception e2) {
        return String.valueOf(event);
      }
    } catch (Exception e) {
      return String.valueOf(event);
    }
  }
}

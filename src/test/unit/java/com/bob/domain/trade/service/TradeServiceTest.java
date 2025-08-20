package com.bob.domain.trade.service;

import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND_WITH_REASON;
import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_ID_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADES;
import static com.bob.support.fixture.domain.TradeFixture.REQUESTED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.RESERVED_TRADE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.CUSTOM_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.CUSTOM_POST_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.PostResponseFixture.DEFAULT_POST_DETAIL_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.dto.response.TradesResponse;
import com.bob.domain.trade.service.port.out.TradeMemberPort;
import com.bob.domain.trade.service.port.out.TradePostPort;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
  private TradeMemberPort memberPort;

  @Mock
  private TradePostPort postPort;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Test
  @DisplayName("거래 생성 시 ID가 반환된다")
  void 거래가_생성되면_거래_ID를_반환한다() {
    // given
    CreateTradeCommand command = DEFAULT_CREATE_TRADE_COMMAND();
    Trade trade = DEFAULT_ID_TRADE(command);

    given(tradeRepository.save(any(Trade.class))).willReturn(trade);

    // when
    Long tradeId = tradeService.createTradeProcess(command);

    // then
    assertThat(tradeId).isEqualTo(1L);
    then(tradeRepository).should(times(1)).save(any(Trade.class));
  }

  @Test
  @DisplayName("거래 목록 조회 - 성공 테스트")
  void 게시글_소유자는_거래_목록을_정상_조회할_수_있다() {
    // given
    UUID requesterId = MEMBER_ID;
    Long postId = 1L;
    ReadTradesQuery query = new ReadTradesQuery(postId, requesterId);
    given(postPort.readTradePostSummary(postId)).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByPostId(postId)).willReturn(DEFAULT_TRADES());
    given(memberPort.readTradeMemberProfile(any(UUID.class)))
        .willAnswer(invocation -> CUSTOM_MEMBER_PROFILE_RESPONSE(invocation.getArgument(0)));

    // when
    TradesResponse response = tradeService.readTradesProcess(query);

    // then
    assertThat(response).isNotNull();
    assertThat(response.trades()).hasSize(3);
    then(postPort).should(times(1)).readTradePostSummary(postId);
    then(tradeReader).should(times(1)).readTradesByPostId(postId);
    then(memberPort).should(times(3)).readTradeMemberProfile(any(UUID.class));
  }

  @Test
  @DisplayName("거래 목록 조회 - 실패 테스트 (게시글 소유자가 아님)")
  void 게시글_소유자가_아니면_거래_목록_조회_시_예외가_발생한다() {
    // given
    UUID postOwnerId = MEMBER_ID;
    UUID requesterId = UUID.randomUUID();
    Long postId = 1L;
    ReadTradesQuery query = new ReadTradesQuery(postId, requesterId);
    given(postPort.readTradePostSummary(postId)).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));

    // when & then
    assertThatThrownBy(() -> tradeService.readTradesProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(TRADE_ACCESS_DENIED.getMessage());

    then(postPort).should(times(1)).readTradePostSummary(postId);
    then(tradeReader).shouldHaveNoInteractions();
    then(memberPort).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("거래 상세 조회 - 성공 테스트")
  void 거래_상세조회_시_판매자인_경우_성공한다() {
    // given
    Trade trade = DEFAULT_ID_TRADE(DEFAULT_CREATE_TRADE_COMMAND());
    UUID sellerId = trade.getSellerId();
    Long tradeId = trade.getId();

    given(tradeReader.readTradeById(tradeId)).willReturn(trade);

    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, sellerId);

    // when
    TradeDetailResponse response = tradeService.readTradeDetailProcess(query);

    // then
    assertThat(response).isNotNull();
    then(tradeReader).should(times(1)).readTradeById(tradeId);
  }

  @Test
  @DisplayName("거래 상세 조회 - 구매자라면 성공한다")
  void 거래_상세조회_시_구매자인_경우_성공한다() {
    // given
    Trade trade = DEFAULT_ID_TRADE(DEFAULT_CREATE_TRADE_COMMAND());
    UUID buyerId = trade.getBuyerId();
    Long tradeId = trade.getId();

    given(tradeReader.readTradeById(tradeId)).willReturn(trade);
    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, buyerId);

    // when
    TradeDetailResponse response = tradeService.readTradeDetailProcess(query);

    // then
    assertThat(response).isNotNull();
    then(tradeReader).should(times(1)).readTradeById(tradeId);
  }

  @Test
  @DisplayName("거래 상세 조회 - 실패 테스트")
  void 거래_상세조회_비참여자라면_예외발생() {
    // given
    Trade trade = DEFAULT_ID_TRADE(DEFAULT_CREATE_TRADE_COMMAND());
    UUID nonParticipantId = UUID.randomUUID();
    Long tradeId = trade.getId();

    given(tradeReader.readTradeById(tradeId)).willReturn(trade);

    ReadTradeDetailQuery query = new ReadTradeDetailQuery(tradeId, nonParticipantId);

    // when & then
    assertThatThrownBy(() -> tradeService.readTradeDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ACCESS_DENIED.getMessage());

    then(tradeReader).should(times(1)).readTradeById(tradeId);
  }

  @DisplayName("거래 상태 변경 - 성공 테스트")
  @Test
  void 거래_상태를_정상적으로_변경할_수_있다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);
    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByPostId(requestTrade.getPostId()))
        .willReturn(List.of(REQUESTED_TRADE(1L,1L), REQUESTED_TRADE(2L,1L))); // 대기중인 거래만 존재

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
    then(postPort).should().changePostStatus(requestTrade.getPostId(), "IN_PROGRESS");
  }

  @Test
  @DisplayName("거래 상태 변경 - 성공 테스트 (취소 사유 입력)")
  void 거래_상태를_취소로_변경_시_사유가_본문에_포함된다() {
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
    then(postPort).should().changePostStatus(requestTrade.getPostId(), "READY");
    then(eventPublisher).should(times(2)).publishEvent(eventCaptor.capture());

    // 채팅, 알림 이벤트 발행 시 body의 거래 취소 사유 포함 여부 검증
    final List<Object> events = eventCaptor.getAllValues();
    assertThat(events).hasSize(2);
    assertThat(events).allMatch(e -> extractEventBody(e).contains(reason));
    then(eventPublisher).shouldHaveNoMoreInteractions();
  }

  @DisplayName("거래 상태 변경 - 실패 테스트 (이미 처리된 거래 존재)")
  @Test
  void 거래_상태_변경시_이미_처리된_거래가_있으면_예외가_발생한다() {
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

  @DisplayName("거래 상태 변경 - 실패 테스트 (게시글 소유자 아님)")
  @Test
  void 거래_상태_변경시_게시글_소유자가_아니면_예외가_발생한다() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);

    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(CUSTOM_POST_DETAIL_RESPONSE(1L, UUID.randomUUID()));

    // when & then
    assertThatThrownBy(() -> tradeService.changeTradeStatusProcess(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining(TRADE_ACCESS_DENIED.getMessage());

    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
  }

  @DisplayName("거래 상태 변경 - 실패 테스트 (동일한 상태 변경 요청)")
  @Test
  void 거래_상태_변경시_현재_상태와_요청_상태가_동일하면_예외가_발생한다() {
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
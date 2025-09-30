package com.bob.domain.trade.service;

import static com.bob.global.exception.response.ApplicationError.IS_SAME_TRADE_MEMBER;
import static com.bob.global.exception.response.ApplicationError.TRADE_ACCESS_DENIED;
import static com.bob.global.exception.response.ApplicationError.TRADE_ALREADY_PROCESSED;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND;
import static com.bob.support.fixture.command.ChangeTradeStatusCommandFixture.DEFAULT_CHANGE_STATUS_COMMAND_WITH_REASON;
import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.command.CreateTradeCommandFixture.SAME_MEMBER_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADES;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADE_WITH_ID;
import static com.bob.support.fixture.domain.TradeFixture.REQUESTED_TRADE;
import static com.bob.support.fixture.domain.TradeFixture.RESERVED_TRADE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.CUSTOM_MEMBER_PROFILE_RESPONSE;
import static com.bob.support.fixture.response.MemberProfileResponseFixture.DEFAULT_MEMBER_PROFILE_RESPONSE;
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
import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.service.dto.response.PostTradesResponse;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
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
  void 거래_생성_및_저장() {
    // given
    CreateTradeCommand command = DEFAULT_CREATE_TRADE_COMMAND;
    given(tradeRepository.save(any(Trade.class))).willReturn(DEFAULT_TRADE_WITH_ID);
    given(postPort.readTradePostSummary(command.postId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(memberPort.readTradeMemberProfile(command.buyerId())).willReturn(DEFAULT_MEMBER_PROFILE_RESPONSE);

    // when
    CreateTradeResponse response = tradeService.createTradeProcess(command);

    // then
    assertThat(response.id()).isEqualTo(1L);
    then(tradeRepository).should(times(1)).save(any(Trade.class));
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
  void 거래_목록_조회() {
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
  void 거래_목록_조회_시_게시글_등록자가_아니면_예외가_발생한다() {
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
  void 거래_상세_조회_게시글_등록자() {
    // given
    Trade trade = DEFAULT_TRADE_WITH_ID;
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
  void 거래_상세_조회_교환_요청자() {
    // given
    Trade trade = DEFAULT_TRADE_WITH_ID;
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
  void 거래_상세_조회_시_게시글_등록자_교환_요청자가_아니면_예외가_발생한다() {
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
  }

  @Test
  void 거래_상태_변경() {
    // given
    ChangeTradeStatusCommand command = DEFAULT_CHANGE_STATUS_COMMAND("RESERVED");
    Trade requestTrade = REQUESTED_TRADE(1L, 1L);
    given(tradeReader.readTradeById(command.tradeId())).willReturn(requestTrade);
    given(postPort.readTradePostSummary(requestTrade.getPostId())).willReturn(DEFAULT_POST_DETAIL_RESPONSE(1L));
    given(tradeReader.readTradesByPostId(requestTrade.getPostId())).willReturn(List.of(REQUESTED_TRADE(1L, 1L), REQUESTED_TRADE(2L, 1L))); // 대기중인 거래만 존재

    // when
    tradeService.changeTradeStatusProcess(command);

    // then
    then(tradeReader).should().readTradeById(command.tradeId());
    then(postPort).should().readTradePostSummary(requestTrade.getPostId());
    then(postPort).should().changeTradeProgress(requestTrade.getPostId(), "IN_PROGRESS");
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
  void 거래_상태_변경_시_게시글_소유자가_아니면_예외가_발생한다() {
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

package com.bob.domain.trade.service;

import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_ID_TRADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.service.reader.TradeReader;
import com.bob.global.exception.exceptions.ApplicationException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("거래 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

  @InjectMocks
  private TradeService tradeService;

  @Mock
  private TradeRepository tradeRepository;

  @Mock
  private TradeReader tradeReader;

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

    // expect
    assertThatThrownBy(() -> tradeService.readTradeDetailProcess(query))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("거래에 접근할 권한이 없습니다");

    then(tradeReader).should(times(1)).readTradeById(tradeId);
  }
}
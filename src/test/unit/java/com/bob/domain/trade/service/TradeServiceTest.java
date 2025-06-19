package com.bob.domain.trade.service;

import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_ID_TRADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
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
}
package com.bob.domain.trade.service.reader;

import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_ID_TRADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("TradeReader 테스트")
@ExtendWith(MockitoExtension.class)
class TradeReaderTest {

  @InjectMocks
  private TradeReader tradeReader;

  @Mock
  private TradeRepository tradeRepository;

  @Test
  @DisplayName("ID로 거래 조회 - 성공 테스트")
  void 거래ID로_조회_성공() {
    // given
    Trade trade = DEFAULT_ID_TRADE(DEFAULT_CREATE_TRADE_COMMAND());
    Long tradeId = trade.getId();

    given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

    // when
    Trade result = tradeReader.readTradeById(tradeId);

    // then
    assertThat(result).isEqualTo(trade);
    verify(tradeRepository, times(1)).findById(tradeId);
  }

  @Test
  @DisplayName("ID로 거래 조회 - 실패 테스트")
  void 거래ID로_조회_시_존재하지_않는_거래인_경우_예외발생() {
    // given
    Long tradeId = 999L;

    given(tradeRepository.findById(tradeId)).willReturn(Optional.empty());

    // expect
    assertThatThrownBy(() -> tradeReader.readTradeById(tradeId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_TRADE.getMessage());

    verify(tradeRepository, times(1)).findById(tradeId);
  }
}
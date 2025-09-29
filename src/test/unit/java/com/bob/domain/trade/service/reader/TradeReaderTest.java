package com.bob.domain.trade.service.reader;

import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADES;
import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADE_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bob.domain.trade.entity.Trade;
import com.bob.domain.trade.repository.TradeRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
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
  void ID_기반_거래_조회() {
    // given
    Trade trade = DEFAULT_TRADE_WITH_ID;
    Long tradeId = trade.getId();

    given(tradeRepository.findById(tradeId)).willReturn(Optional.of(trade));

    // when
    Trade result = tradeReader.readTradeById(tradeId);

    // then
    assertThat(result).isEqualTo(trade);
    verify(tradeRepository, times(1)).findById(tradeId);
  }

  @Test
  void ID_기반_거래_조회_시_존재하지_않는_ID이면_예외가_발생한다() {
    // given
    Long tradeId = 999L;

    given(tradeRepository.findById(tradeId)).willReturn(Optional.empty());

    // expect
    assertThatThrownBy(() -> tradeReader.readTradeById(tradeId))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_EXISTS_TRADE.getMessage());

    verify(tradeRepository, times(1)).findById(tradeId);
  }

  @Test
  void 게시글_ID_기반_거래_목록_조회() {
    // given
    Long postId = 1L;
    given(tradeRepository.findAllByPostId(postId)).willReturn(DEFAULT_TRADES());

    // when
    List<Trade> result = tradeReader.readTradesByPostId(postId);

    // then
    assertThat(result).hasSize(3);
    verify(tradeRepository, times(1)).findAllByPostId(postId);
  }
}
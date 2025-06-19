package com.bob.domain.trade.adapter;

import static com.bob.support.fixture.command.CreateTradeCommandFixture.DEFAULT_CREATE_TRADE_COMMAND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.web.trade.adapter.in.ChatTradeAdapter;
import com.bob.domain.trade.service.TradeService;
import com.bob.domain.trade.service.dto.command.CreateTradeCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("거래 Adapter 테스트")
@ExtendWith(MockitoExtension.class)
class TradeAdapterTest {

  @InjectMocks
  private ChatTradeAdapter tradeAdapter;

  @Mock
  private TradeService tradeService;

  @Test
  @DisplayName("거래 생성 기능 호출 테스트")
  void 거래가_생성되면_거래_ID가_반환된다() {
    // given
    Long expect = 1L;
    CreateTradeCommand command = DEFAULT_CREATE_TRADE_COMMAND();
    given(tradeService.createTradeProcess(any(CreateTradeCommand.class))).willReturn(expect);

    // when
    Long tradeId = tradeAdapter.createTrade(command.postId(), command.sellerId(), command.buyerId());

    // then
    assertThat(tradeId).isEqualTo(expect);
    then(tradeService).should(times(1)).createTradeProcess(any(CreateTradeCommand.class));
  }
}
package com.bob.domain.trade.adapter;

import static com.bob.support.fixture.domain.TradeFixture.DEFAULT_TRADE_WITH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.response.TradeDetailResponse;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.web.trade.adapter.in.ChatTradeAdapter;
import java.util.UUID;
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
  private TradeReadUseCase readUseCase;

  @Test
  @DisplayName("채팅 거래 요약 조회 기능 호출 테스트")
  void 채팅_거래요약_조회시_정상적으로_응답된다() {
    // given
    Long tradeId = 1L;
    UUID memberId = UUID.randomUUID();
    TradeDetailResponse expectedResponse = TradeDetailResponse.from(DEFAULT_TRADE_WITH_ID);
    given(readUseCase.readTradeDetailProcess(ReadTradeDetailQuery.of(tradeId, memberId))).willReturn(expectedResponse);

    // when
    TradeDetailResponse response = tradeAdapter.readChatTradeSummary(tradeId, memberId);

    // then
    assertThat(response).isEqualTo(expectedResponse);
    then(readUseCase).should(times(1)).readTradeDetailProcess(ReadTradeDetailQuery.of(tradeId, memberId));
  }
}
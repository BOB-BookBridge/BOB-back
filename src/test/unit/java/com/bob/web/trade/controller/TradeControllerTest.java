package com.bob.web.trade.controller;

import static com.bob.support.fixture.response.TradesResponseFixture.DEFAULT_TRADES_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("거래 목록 조회 API 테스트")
@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

  @InjectMocks
  private TradeController tradeController;

  @Mock
  private TradeReadUseCase tradeReadUseCase;

  @Test
  @DisplayName("거래 목록 조회 API 호출 테스트")
  void 거래_목록을_조회할_수_있다() throws Exception {
    // given
    MockMvc mvc = standaloneSetup(tradeController).build();
    given(tradeReadUseCase.readTradesProcess(any(ReadTradesQuery.class))).willReturn(DEFAULT_TRADES_RESPONSE());

    // when & then
    mvc.perform(get("/trades")
            .param("postId", "1"))
        .andExpect(status().isOk());

    verify(tradeReadUseCase, times(1)).readTradesProcess(any(ReadTradesQuery.class));
  }
}

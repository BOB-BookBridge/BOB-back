package com.bob.web.trade.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.TradesResponseFixture.DEFAULT_TRADES_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("거래 목록 조회 API 테스트")
@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

  @InjectMocks
  private TradeController tradeController;

  @Mock
  private TradeReadUseCase tradeReadUseCase;

  @Mock
  private TradeModifyUseCase tradeModifyUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(tradeController).build();
  }

  @Test
  @DisplayName("거래 목록 조회 API 호출 테스트")
  void 거래_목록을_조회할_수_있다() throws Exception {
    // given
    given(tradeReadUseCase.readTradesProcess(any(ReadTradesQuery.class))).willReturn(DEFAULT_TRADES_RESPONSE());

    // when & then
    mvc.perform(get("/trades")
            .param("postId", "1"))
        .andExpect(status().isOk());

    verify(tradeReadUseCase, times(1)).readTradesProcess(any(ReadTradesQuery.class));
  }

  @Test
  @DisplayName("거래 상태 변경 API 호출 테스트")
  void 거래_상태를_변경할_수_있다() throws Exception {
    // given
    Long tradeId = 1L;
    String json = """
        {
          "buyerId": "00000000-0000-0000-0000-000000000000",
          "status": "RESERVED"
        }
        """;

    // when & then
    mvc.perform(patch("/trades/{tradeId}", tradeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("UPDATED"));
    // then
    verify(tradeModifyUseCase, times(1)).changeTradeStatusProcess(any());
  }
}

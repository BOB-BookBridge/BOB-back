package com.bob.web.trade.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.trade.TradeDetailResponseFixture.DEFAULT_TRADE_DETAIL_RESPONSE;
import static com.bob.support.fixture.response.trade.TradesResponseFixture.RECEIVED_TRADES_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.trade.service.dto.command.ChangeTradeItemsCommand;
import com.bob.domain.trade.service.dto.command.ChangeTradeStatusCommand;
import com.bob.domain.trade.service.dto.query.ReadTradeDetailQuery;
import com.bob.domain.trade.service.dto.query.ReadTradesQuery;
import com.bob.domain.trade.service.dto.response.ChangeTradeStatusResult;
import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
import com.bob.domain.trade.usecase.TradeDeleteUseCase;
import com.bob.domain.trade.usecase.TradeModifyUseCase;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import com.bob.domain.trade.usecase.TradeWriteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("거래 목록 조회 API 테스트")
@ExtendWith(MockitoExtension.class)
class TradeControllerTest {

  @InjectMocks
  private TradeController tradeController;

  @Mock
  private TradeWriteUseCase writeUseCase;

  @Mock
  private TradeReadUseCase readUseCase;

  @Mock
  private TradeModifyUseCase modifyUseCase;

  @Mock
  private TradeDeleteUseCase deleteUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(tradeController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  void 거래_생성_기능_호출() throws Exception {
    // given
    String json = """
        {
          "postId": 1,
          "itemIds": [10, 11],
          "isFar": false
        }
        """;
    given(writeUseCase.createTradeProcess(any())).willReturn(mock(CreateTradeResponse.class));

    // when & then
    mvc.perform(post("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isCreated());

    verify(writeUseCase, times(1)).createTradeProcess(any());
  }

  @Test
  void 거래_목록_조회_기능_호출() throws Exception {
    // given
    given(readUseCase.readTradesProcess(any(ReadTradesQuery.class), any())).willReturn(RECEIVED_TRADES_RESPONSE);

    // when & then
    mvc.perform(get("/trades", 1L)
            .param("key", "all")
            .param("status", "all")
            .param("page", "0")
            .param("size", "12"))
        .andExpect(status().isOk());

    verify(readUseCase, times(1)).readTradesProcess(any(ReadTradesQuery.class), any());
  }

  @Test
  void 거래_상세_조회_기능_호출() throws Exception {
    // given
    given(readUseCase.readTradeDetailProcess(any(ReadTradeDetailQuery.class))).willReturn(DEFAULT_TRADE_DETAIL_RESPONSE);

    // when & then
    mvc.perform(get("/trades/{tradeId}", 1L)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk());

    verify(readUseCase, times(1)).readTradeDetailProcess(any(ReadTradeDetailQuery.class));
  }

  @Test
  void 거래_상태_변경_기능_호출() throws Exception {
    // given
    Long tradeId = 1L;
    String json = """
        {
          "buyerId": "00000000-0000-0000-0000-000000000000",
          "status": "RESERVED",
          "reason": null
        }
        """;
    ChangeTradeStatusResult result = ChangeTradeStatusResult.of(1L);
    given(modifyUseCase.changeTradeStatusProcess(any(ChangeTradeStatusCommand.class))).willReturn(result);

    // when & then
    mvc.perform(patch("/trades/{tradeId}", tradeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.chatroomId").value(result.chatroomId()));

    // then
    verify(modifyUseCase, times(1)).changeTradeStatusProcess(any());
  }

  @Test
  void 거래_물품_변경_기능_호출() throws Exception {
    // given
    Long tradeId = 1L;
    String json = """
        {
          "itemIds": [1, 2]
        }
        """;

    // when & then
    mvc.perform(patch("/trades/{tradeId}/items", tradeId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("UPDATED"));
    // then
    verify(modifyUseCase, times(1)).changeTradeItemProcess(any(ChangeTradeItemsCommand.class));
  }

  @Test
  void 거래_삭제_기능_호출() throws Exception {
    // given
    Long tradeId = 1L;

    // when & then
    mvc.perform(delete("/trades/{tradeId}", tradeId)
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("DELETED"));

    // verify
    verify(deleteUseCase, times(1)).deleteTradeProcess(any());
  }
}

package com.bob.web.trade.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.trade.service.dto.response.CreateTradeResponse;
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

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(tradeController).build();
  }

  @Test
  void 거래_생성_기능_호출() throws Exception {
    // given
    String json = """
        {
          "postId": 1,
          "memberBookIds": [10, 11]
        }
        """;
    given(writeUseCase.createTradeProcess(any())).willReturn(mock(CreateTradeResponse.class));

    // when & then
    mvc.perform(post("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
            .requestAttr("memberId", MEMBER_ID)
        )
        .andExpect(status().isCreated());

    verify(writeUseCase, times(1)).createTradeProcess(any());
  }

  @Test
  void 거래_상태_변경_기능_호출() throws Exception {
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
    verify(modifyUseCase, times(1)).changeTradeStatusProcess(any());
  }
}

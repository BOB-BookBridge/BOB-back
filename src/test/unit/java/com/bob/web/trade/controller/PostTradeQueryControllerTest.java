package com.bob.web.trade.controller;

import static com.bob.support.fixture.response.PostTradesResponseFixture.DEFAULT_POST_TRADES_RESPONSE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.trade.service.dto.query.ReadPostTradesQuery;
import com.bob.domain.trade.usecase.TradeReadUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("게시글 거래 목록 조회 API 테스트")
@ExtendWith(MockitoExtension.class)
class PostTradeQueryControllerTest {

  @InjectMocks
  private PostTradeQueryController postTradeQueryController;

  @Mock
  private TradeReadUseCase readUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(postTradeQueryController).build();
  }

  @Test
  void 게시글_거래_목록_조회_기능_호출() throws Exception {
    // given
    given(readUseCase.readPostTradesProcess(any(ReadPostTradesQuery.class))).willReturn(DEFAULT_POST_TRADES_RESPONSE());

    // when & then
    mvc.perform(get("/posts/{postId}/trades", 1L))
        .andExpect(status().isOk());

    verify(readUseCase, times(1)).readPostTradesProcess(any(ReadPostTradesQuery.class));
  }
}
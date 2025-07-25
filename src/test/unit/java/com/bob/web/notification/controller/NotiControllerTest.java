package com.bob.web.notification.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static com.bob.support.fixture.response.NotificationsResponseFixture.FILTERING_NOTIFICATIONS_RESPONSE;
import static com.bob.web.common.symbol.ResponseSymbol.UPDATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bob.domain.notification.service.dto.query.ReadNotificationsQuery;
import com.bob.domain.notification.usecase.NotiModifyUseCase;
import com.bob.domain.notification.usecase.NotiReadUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@DisplayName("알림 목록 조회 API 테스트")
@ExtendWith(MockitoExtension.class)
class NotiControllerTest {

  @InjectMocks
  private NotiController notiController;

  @Mock
  private NotiReadUseCase readUseCase;

  @Mock
  private NotiModifyUseCase modifyUseCase;

  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc = MockMvcBuilders.standaloneSetup(notiController).build();
  }

  @Test
  @DisplayName("알림 목록 조회 API 호출 테스트")
  void 알림_목록을_조회할_수_있다() throws Exception {
    // given
    given(readUseCase.readNotificationsProcess(any(ReadNotificationsQuery.class)))
        .willReturn(FILTERING_NOTIFICATIONS_RESPONSE());

    // when & then
    mvc.perform(get("/notifications")
            .requestAttr("memberId", MEMBER_ID))
        .andExpect(status().isOk());

    // verify
    verify(readUseCase, times(1)).readNotificationsProcess(any(ReadNotificationsQuery.class));
  }

  @Test
  @DisplayName("알림 읽음 처리 API 호출 테스트")
  void 알림을_읽음처리_할_수_있다() throws Exception {
    // given
    Long notificationId = 1L;

    // when & then
    mvc.perform(patch("/notifications/{notificationId}", notificationId)
            .requestAttr("memberId", MEMBER_ID)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.result").value("UPDATED"));

    verify(modifyUseCase, times(1)).changeNotificationReadStatusProcess(any());
  }
}

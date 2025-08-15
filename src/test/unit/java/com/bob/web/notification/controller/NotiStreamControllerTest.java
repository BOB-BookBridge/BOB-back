package com.bob.web.notification.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@DisplayName("알림 SSE 구독 API 테스트")
@ExtendWith(MockitoExtension.class)
class NotiStreamControllerTest {

  @InjectMocks
  private NotiStreamController controller;

  @Mock
  private EmitterManager emitterManager;

  @Test
  @DisplayName("SSE 구독 - 성공 테스트")
  void 채팅방_SSE_구독에_성공한다() {
    // given
    given(emitterManager.subscribeToNoti(MEMBER_ID)).willReturn(new SseEmitter());

    // when
    SseEmitter emitter = controller.handleSubscribeNotification(MEMBER_ID);

    // then
    then(emitterManager).should().subscribeToNoti(MEMBER_ID);
    assert emitter != null;
  }
}
package com.bob.web.notification.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.infra.auth.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
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

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private HttpServletRequest request;

  private String token = "mock.auth.token";

  @BeforeEach
  void setup() {
    Cookie cookie = new Cookie("AUTHORIZATION", token);
    given(request.getCookies()).willReturn(new Cookie[]{cookie});
  }

  @Test
  @DisplayName("SSE 구독 - 성공 테스트")
  void 채팅방_SSE_구독에_성공한다() {
    // given
    given(jwtProvider.isVerified(token)).willReturn(true);
    given(jwtProvider.getMemberId(token)).willReturn(MEMBER_ID);
    given(emitterManager.subscribeToNoti(MEMBER_ID)).willReturn(new SseEmitter());

    // when
    SseEmitter emitter = controller.handleSubscribeNotification(request);

    // then
    then(jwtProvider).should().isVerified(token);
    then(jwtProvider).should().getMemberId(token);
    then(emitterManager).should().subscribeToNoti(MEMBER_ID);
    assert emitter != null;
  }

  @Test
  @DisplayName("SSE 구독 - 실패 테스트(토큰 없음)")
  void 토큰이_없으면_예외가_발생한다() {
    // given
    given(request.getCookies()).willReturn(null);

    // when & then
    assertThatThrownBy(() -> controller.handleSubscribeNotification(request))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.AUTHENTICATION_FAILED.getMessage());

    then(jwtProvider).shouldHaveNoInteractions();
    then(emitterManager).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("SSE 구독 - 실패 테스트(토큰 검증 실패)")
  void 인증되지_않은_토큰이면_예외가_발생한다() {
    // given
    given(jwtProvider.isVerified(token)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> controller.handleSubscribeNotification(request))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.AUTHENTICATION_FAILED.getMessage());

    then(jwtProvider).should().isVerified(token);
    then(jwtProvider).shouldHaveNoMoreInteractions();
    then(emitterManager).shouldHaveNoInteractions();
  }
}
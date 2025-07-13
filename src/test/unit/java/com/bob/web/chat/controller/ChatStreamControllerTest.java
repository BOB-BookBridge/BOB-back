package com.bob.web.chat.controller;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ValidateParticipantQuery;
import com.bob.domain.chat.usecase.ChatRoomModifyUseCase;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
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

@DisplayName("채팅 SSE 구독 API 테스트")
@ExtendWith(MockitoExtension.class)
class ChatStreamControllerTest {

  @InjectMocks
  private ChatStreamController controller;

  @Mock
  private ChatRoomReadUseCase readUseCase;

  @Mock
  private ChatRoomModifyUseCase modifyUseCase;

  @Mock
  private EmitterManager emitterManager;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private HttpServletRequest request;

  private final String token = "mock.auth.token";

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
    given(emitterManager.subscribeToChat(1L, MEMBER_ID)).willReturn(new SseEmitter());

    // when
    SseEmitter emitter = controller.handleSubscribeChat(1L, request);

    // then
    then(jwtProvider).should().isVerified(token);
    then(jwtProvider).should().getMemberId(token);
    then(readUseCase).should().validateParticipant(ValidateParticipantQuery.of(1L, MEMBER_ID));
    then(modifyUseCase).should().enterChatRoomProcess(EnterChatRoomCommand.of(1L, MEMBER_ID));
    then(emitterManager).should().subscribeToChat(1L, MEMBER_ID);
    assert emitter != null;
  }

  @Test
  @DisplayName("SSE 구독 - 실패 테스트(토큰 없음)")
  void 토큰이_없으면_예외가_발생한다() {
    // given
    given(request.getCookies()).willReturn(null);

    // when & then
    assertThatThrownBy(() -> controller.handleSubscribeChat(1L, request))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.AUTHENTICATION_FAILED.getMessage());

    then(jwtProvider).shouldHaveNoInteractions();
    then(readUseCase).shouldHaveNoInteractions();
    then(emitterManager).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("SSE 구독 - 실패 테스트(토큰 검증 실패)")
  void 인증되지_않은_토큰이면_예외가_발생한다() {
    // given
    given(jwtProvider.isVerified(token)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> controller.handleSubscribeChat(1L, request))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.AUTHENTICATION_FAILED.getMessage());

    then(jwtProvider).should().isVerified(token);
    then(jwtProvider).shouldHaveNoMoreInteractions();
    then(readUseCase).shouldHaveNoInteractions();
    then(emitterManager).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("SSE 구독 - 실패 테스트(채팅방 참가자가 아님)")
  void 채팅방_참가자가_아니면_예외가_발생한다() {
    // given
    given(jwtProvider.isVerified(token)).willReturn(true);
    given(jwtProvider.getMemberId(token)).willReturn(MEMBER_ID);
    willThrow(new ApplicationException(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM))
        .given(readUseCase)
        .validateParticipant(ValidateParticipantQuery.of(1L, MEMBER_ID));

    // when & then
    assertThatThrownBy(() -> controller.handleSubscribeChat(1L, request))
        .isInstanceOf(ApplicationException.class)
        .hasMessage(ApplicationError.NOT_PARTICIPATED_CHAT_ROOM.getMessage());

    then(jwtProvider).should().isVerified(token);
    then(jwtProvider).should().getMemberId(token);
    then(readUseCase).should().validateParticipant(ValidateParticipantQuery.of(1L, MEMBER_ID));
    then(emitterManager).shouldHaveNoInteractions();
  }
}
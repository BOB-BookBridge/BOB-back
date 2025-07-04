package com.bob.global.event.sse.manager;

import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmitterManager 테스트")
class EmitterManagerTest {

  @InjectMocks
  private EmitterManager emitterManager;

  @Mock
  private EmitterRepository<String> notificationEmitterRepository;

  @Mock
  private EmitterRepository<ChatEmitterKey> chatEmitterRepository;

  @Mock
  private ScheduledExecutorService sseHeartbeatScheduler;

  @Test
  @DisplayName("SSE 이벤트 전송 - 성공 테스트")
  void sendEvent_정상적으로_보낼_수_있다() throws Exception {
    // given
    SseEmitter emitter = mock(SseEmitter.class);
    String key = "member-key";

    // when
    emitterManager.sendEvent(key, emitter, "connect", "data", notificationEmitterRepository);

    // then
    then(emitter).should().send(any(SseEmitter.SseEventBuilder.class));
    then(notificationEmitterRepository).should(never()).remove(key);
  }

  @Test
  @DisplayName("SSE 이벤트 전송 - 실패 테스트")
  void sendEvent_실패하면_emitter_완료_및_제거() throws Exception {
    // given
    SseEmitter emitter = mock(SseEmitter.class);
    String key = "fail-key";

    willThrow(new RuntimeException("send 실패")).given(emitter).send(any(SseEmitter.SseEventBuilder.class));

    // when
    emitterManager.sendEvent(key, emitter, "error", "fail", notificationEmitterRepository);

    // then
    then(emitter).should().complete();
    then(notificationEmitterRepository).should().remove(key);
  }

  @Test
  @DisplayName("run() 호출 테스트")
  void run_호출시_sendEvent_호출_확인() {
    // given
    EmitterManager manager = spy(new EmitterManager(notificationEmitterRepository, chatEmitterRepository, sseHeartbeatScheduler));
    ReflectionTestUtils.setField(manager, "defaultTimeout", 180000L);
    ReflectionTestUtils.setField(manager, "heartbeatInterval", 10L);

    SseEmitter emitter = mock(SseEmitter.class);
    given(notificationEmitterRepository.findAll()).willReturn(Map.of("member-id", emitter));
    given(chatEmitterRepository.findAll()).willReturn(Map.of());

    // when
    manager.run();

    // then
    verify(manager).sendEvent(
        eq("member-id"),
        eq(emitter),
        eq("heartbeat"),
        eq("ping"),
        eq(notificationEmitterRepository)
    );
  }

  @Test
  @DisplayName("채팅 SSE 구독 - emitter 저장 및 connect 이벤트 전송 테스트")
  void subscribeToChat_정상_동작한다() {
    // given
    EmitterManager manager = spy(new EmitterManager(notificationEmitterRepository, chatEmitterRepository, sseHeartbeatScheduler));
    ChatEmitterKey expectedKey = new ChatEmitterKey(1L, MEMBER_ID);
    ReflectionTestUtils.setField(emitterManager, "defaultTimeout", 180_000L);

    // when
    SseEmitter emitter = manager.subscribeToChat(1L, MEMBER_ID);

    // then
    then(chatEmitterRepository).should().save(eq(expectedKey), eq(emitter));
    then(manager).should().sendEvent(
        eq(expectedKey),
        eq(emitter),
        eq("connect"),
        eq("connected"),
        eq(chatEmitterRepository)
    );
  }

  @Test
  @DisplayName("중복 emitter 존재가 존재하는 경우 삭제 테스트")
  void 동일한_emitter_존재시_제거한다() {
    // given
    ChatEmitterKey key = new ChatEmitterKey(1L, MEMBER_ID);
    SseEmitter existingEmitter = mock(SseEmitter.class);
    given(chatEmitterRepository.get(key)).willReturn(existingEmitter);

    // when
    ReflectionTestUtils.invokeMethod(emitterManager, "verifyDuplicateEmitter", key, chatEmitterRepository);

    // then
    then(existingEmitter).should().complete();
    then(chatEmitterRepository).should().remove(key);
  }
}
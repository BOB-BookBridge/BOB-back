package com.bob.global.event.sse.manager;

import static com.bob.global.event.sse.manager.type.EmitEventType.CONNECT;
import static com.bob.global.event.sse.manager.type.EmitterType.NOTIFICATION;
import static com.bob.support.fixture.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.doThrow;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmitterManager 테스트")
class EmitterManagerTest {

  private EmitterManager emitterManager;

  @Mock
  private EmitterRepository<NotiEmitterKey> notificationEmitterRepository;

  @Mock
  private EmitterRepository<ChatEmitterKey> chatEmitterRepository;

  @Mock
  private ScheduledExecutorService sseHeartbeatScheduler;

  @BeforeEach
  void setUp() {
    emitterManager = new EmitterManager(notificationEmitterRepository, chatEmitterRepository, sseHeartbeatScheduler);
    ReflectionTestUtils.setField(emitterManager, "defaultTimeout", 180_000L);
    ReflectionTestUtils.setField(emitterManager, "heartbeatInterval", 10L);
  }

  @Test
  @DisplayName("SSE 이벤트 전송 - 성공 테스트")
  void sendEvent_호출_시_이벤트가_정상_전송된다() throws Exception {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    SseEmitter emitter = mock(SseEmitter.class);
    given(notificationEmitterRepository.get(key)).willReturn(emitter);

    // when
    emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "connected");

    // then
    then(emitter).should().send(any(SseEmitter.SseEventBuilder.class));
  }

  @Test
  @DisplayName("SSE 이벤트 전송 - 실패 테스트(예외 발생 시 emitter 제거)")
  void sendEvent_실패시_emitter_제거한다() throws Exception {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    SseEmitter emitter = spy(new SseEmitter(180000L));
    doThrow(new RuntimeException("send 실패")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));
    given(notificationEmitterRepository.get(key)).willReturn(emitter);

    // when
    emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "data");

    // then
    then(emitter).should().complete();
    then(notificationEmitterRepository).should().remove(key);
  }

  @Test
  @DisplayName("채팅 SSE 구독 - emitter 저장 및 connect 이벤트 전송 테스트")
  void subscribeToChat_호출_시_정상_구독_및_connect_이벤트를_전송한다() {
    // given
    ChatEmitterKey key = new ChatEmitterKey(1L, MEMBER_ID);
    given(chatEmitterRepository.get(key)).willReturn(null);

    // when
    SseEmitter emitter = emitterManager.subscribeToChat(1L, MEMBER_ID);

    // then
    then(chatEmitterRepository).should().save(eq(key), eq(emitter));
  }

  @Test
  @DisplayName("알림 SSE 구독 - emitter 저장 및 connect 이벤트 전송 테스트")
  void subscribeToNoti_호출_시_정상_구독_및_connect_이벤트를_전송한다() {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    given(notificationEmitterRepository.get(key)).willReturn(null);

    // when
    SseEmitter emitter = emitterManager.subscribeToNoti(MEMBER_ID);

    // then
    then(notificationEmitterRepository).should().save(eq(key), eq(emitter));
  }

  @Test
  @DisplayName("subscribe() 호출 시 기존 emitter가 존재하면 제거 후 새 emitter 저장")
  void subscribe_기존_emitter_존재시_제거_후_저장() {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    SseEmitter oldEmitter = mock(SseEmitter.class);
    given(notificationEmitterRepository.get(key)).willReturn(oldEmitter);

    // when
    emitterManager.subscribeToNoti(MEMBER_ID);

    // then
    then(oldEmitter).should().complete();
    then(notificationEmitterRepository).should().remove(key);
    then(notificationEmitterRepository).should().save(eq(key), any(SseEmitter.class));
  }

  @Test
  @DisplayName("run() 호출 테스트 - 모든 emitter에 heartbeat 전송")
  void run_호출시_heartbeat가_전송된다() throws Exception {
    // given
    SseEmitter emitter = mock(SseEmitter.class);
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    given(notificationEmitterRepository.findAll()).willReturn(Map.of(key, emitter));
    given(chatEmitterRepository.findAll()).willReturn(Map.of());

    // when
    emitterManager.run();

    // then
    then(emitter).should().send(any(SseEmitter.SseEventBuilder.class));
  }

  @Test
  @DisplayName("emitter 존재 여부 검사 - 정상 작동중인 emitter의 경우 true 반환")
  void 정상_작동중인_Emitter는_true를_반환한다() {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    SseEmitter emitter = mock(SseEmitter.class);
    given(notificationEmitterRepository.get(key)).willReturn(emitter);

    // when
    boolean exists = emitterManager.isExistClientConnection(NOTIFICATION, key);

    // then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("emitter 존재 여부 검사 - 존재하지만 event 발송 실패 시 false 반환")
  void Emitter가_존재하지만_이벤트를_보낼_수_없는_경우_false를_반환하고_emitter를_삭제한다() throws IOException {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    SseEmitter emitter = mock(SseEmitter.class);
    given(notificationEmitterRepository.get(key)).willReturn(emitter);
    doThrow(new RuntimeException("send 실패")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));

    // when
    boolean exists = emitterManager.isExistClientConnection(NOTIFICATION, key);

    // then
    assertThat(exists).isFalse();
    then(notificationEmitterRepository).should(times(1)).remove(key);
  }

  @Test
  @DisplayName("SSE 이벤트 전송 - emitter가 존재하지 않으면 이벤트 전송 생략")
  void sendEvent_emitter_null일_경우_무시() {
    // given
    NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
    given(notificationEmitterRepository.get(key)).willReturn(null);

    // when
    emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "connected");

    // then
    then(notificationEmitterRepository).should(never()).remove(any());
  }
}

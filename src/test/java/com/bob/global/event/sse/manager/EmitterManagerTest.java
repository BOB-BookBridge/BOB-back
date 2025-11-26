package com.bob.global.event.sse.manager;

import static com.bob.global.event.sse.manager.type.EmitEventType.CONNECT;
import static com.bob.global.event.sse.manager.type.EmitterType.NOTIFICATION;
import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
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

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;

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
        emitterManager = new EmitterManager(notificationEmitterRepository, chatEmitterRepository,
            sseHeartbeatScheduler);
        ReflectionTestUtils.setField(emitterManager, "defaultTimeout", 180_000L);
        ReflectionTestUtils.setField(emitterManager, "heartbeatInterval", 10L);
    }

    @Test
    void 이벤트_전송() throws Exception {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        SseEmitter emitter = mock(SseEmitter.class);
        given(notificationEmitterRepository.get(key)).willReturn(emitter);

        emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "connected");

        then(emitter).should().send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void 이벤트_전송_시_Emitter가_존재하지_않으면_전송_생략() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        given(notificationEmitterRepository.get(key)).willReturn(null);

        emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "connected");

        then(notificationEmitterRepository).should(never()).remove(any());
    }

    @Test
    void 이벤트_전송__실패_시_emitter_제거() throws Exception {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        SseEmitter emitter = spy(new SseEmitter(180000L));
        doThrow(new RuntimeException("send 실패")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));
        given(notificationEmitterRepository.get(key)).willReturn(emitter);

        emitterManager.sendEvent(NOTIFICATION, key, CONNECT, "data");

        then(emitter).should().complete();
        then(notificationEmitterRepository).should().remove(key);
    }

    @Test
    void 채팅_이벤트_구독() {
        ChatEmitterKey key = new ChatEmitterKey(1L, MEMBER_ID);
        given(chatEmitterRepository.get(key)).willReturn(null);

        SseEmitter emitter = emitterManager.subscribeToChat(1L, MEMBER_ID);

        then(chatEmitterRepository).should().save(eq(key), eq(emitter));
    }

    @Test
    void 알림_이벤트_구독() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        given(notificationEmitterRepository.get(key)).willReturn(null);

        SseEmitter emitter = emitterManager.subscribeToNoti(MEMBER_ID);

        then(notificationEmitterRepository).should().save(eq(key), eq(emitter));
    }

    @Test
    void Emitter_중복_제거() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        SseEmitter oldEmitter = mock(SseEmitter.class);
        given(notificationEmitterRepository.get(key)).willReturn(oldEmitter);

        emitterManager.subscribeToNoti(MEMBER_ID);

        then(oldEmitter).should().complete();
        then(notificationEmitterRepository).should().remove(key);
        then(notificationEmitterRepository).should().save(eq(key), any(SseEmitter.class));
    }

    @Test
    void heartbeat_전송() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        given(notificationEmitterRepository.findAll()).willReturn(Map.of(key, emitter));
        given(chatEmitterRepository.findAll()).willReturn(Map.of());

        emitterManager.run();

        then(emitter).should().send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    void Emitter_동작_검증() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        SseEmitter emitter = mock(SseEmitter.class);
        given(notificationEmitterRepository.get(key)).willReturn(emitter);

        boolean exists = emitterManager.isExistConnection(NOTIFICATION, key);

        assertThat(exists).isTrue();
    }

    @Test
    void Emitter_동작_검증_시_이벤트_전송_실패하면_Emitter_제거() throws IOException {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);
        SseEmitter emitter = mock(SseEmitter.class);
        given(notificationEmitterRepository.get(key)).willReturn(emitter);
        doThrow(new RuntimeException("send 실패")).when(emitter).send(any(SseEmitter.SseEventBuilder.class));

        boolean exists = emitterManager.isExistConnection(NOTIFICATION, key);

        assertThat(exists).isFalse();
        then(notificationEmitterRepository).should(times(1)).remove(key);
    }
}

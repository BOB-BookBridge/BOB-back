package com.bob.core.adapter.notification.api;

import static com.bob.support.fixture.member.domain.MemberFixture.MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import com.bob.support.annotation.BobApiTest;

@DisplayName("알림 구독 API 테스트")
@BobApiTest
record NotificationStreamApiTest(
    NotificationStreamApi notificationStreamApi, EmitterRepository<NotiEmitterKey> emitterRepository
) {

    @Test
    void 알림_구독() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);

        SseEmitter emitter = notificationStreamApi.subscribe(MEMBER_ID);

        assertThat(emitter).isNotNull();
        assertThat(emitterRepository.exists(key)).isTrue();

        emitter.complete();
    }

    @Test
    void 알림_구독_시_중복_연결_제거() {
        NotiEmitterKey key = new NotiEmitterKey(MEMBER_ID);

        SseEmitter firstEmitter = notificationStreamApi.subscribe(MEMBER_ID);
        assertThat(emitterRepository.exists(key)).isTrue();

        SseEmitter secondEmitter = notificationStreamApi.subscribe(MEMBER_ID);
        assertThat(emitterRepository.exists(key)).isTrue();
        assertThat(emitterRepository.findAll()).hasSize(1);

        assertThat(firstEmitter).isNotEqualTo(secondEmitter);

        secondEmitter.complete();
    }
}

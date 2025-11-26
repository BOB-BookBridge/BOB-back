package com.bob.global.event.sse.repository.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
@DisplayName("알림 SSE 저장소 테스트")
class NotificationEmitterRepositoryTest {

    @InjectMocks
    private NotificationEmitterRepository repository;

    @Test
    void Emitter_저장() {
        NotiEmitterKey key = new NotiEmitterKey(UUID.randomUUID());
        SseEmitter emitter = new SseEmitter();

        repository.save(key, emitter);

        assertThat(repository.get(key)).isEqualTo(emitter);
    }

    @Test
    void Emitter_존재_여부_확인() {
        NotiEmitterKey key = new NotiEmitterKey(UUID.randomUUID());
        NotiEmitterKey invalid = new NotiEmitterKey(UUID.randomUUID());
        SseEmitter emitter = new SseEmitter();
        repository.save(key, emitter);

        assertThat(repository.exists(key)).isTrue();
        assertThat(repository.exists(invalid)).isFalse();
    }

    @Test
    void Emitter_전체_조회() {
        NotiEmitterKey key1 = new NotiEmitterKey(UUID.randomUUID());
        NotiEmitterKey key2 = new NotiEmitterKey(UUID.randomUUID());
        repository.save(key1, new SseEmitter());
        repository.save(key2, new SseEmitter());

        Map<NotiEmitterKey, SseEmitter> allEmitters = repository.findAll();

        assertThat(allEmitters).hasSize(2).containsKeys(key1, key2);
    }

    @Test
    void Emitter_삭제() {
        NotiEmitterKey key = new NotiEmitterKey(UUID.randomUUID());
        repository.save(key, new SseEmitter());

        repository.remove(key);

        assertThat(repository.exists(key)).isFalse();
        assertThat(repository.get(key)).isNull();
    }
}

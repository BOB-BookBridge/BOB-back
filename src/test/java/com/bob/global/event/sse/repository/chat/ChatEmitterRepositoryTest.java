package com.bob.global.event.sse.repository.chat;

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
@DisplayName("채팅 SSE 저장소 테스트")
class ChatEmitterRepositoryTest {

    @InjectMocks
    private ChatEmitterRepository repository;

    @Test
    void Emitter_저장_및_조회() {
        ChatEmitterKey key = new ChatEmitterKey(1L, UUID.randomUUID());
        SseEmitter emitter = new SseEmitter();

        repository.save(key, emitter);
        SseEmitter result = repository.get(key);

        assertThat(result).isEqualTo(emitter);
    }

    @Test
    void Emitter_존재_여부_확인() {
        ChatEmitterKey key = new ChatEmitterKey(2L, UUID.randomUUID());
        SseEmitter emitter = new SseEmitter();
        repository.save(key, emitter);

        assertThat(repository.exists(key)).isTrue();
    }

    @Test
    void Emitter_삭제() {
        ChatEmitterKey key = new ChatEmitterKey(3L, UUID.randomUUID());
        SseEmitter emitter = new SseEmitter();
        repository.save(key, emitter);

        repository.remove(key);

        assertThat(repository.exists(key)).isFalse();
        assertThat(repository.get(key)).isNull();
    }

    @Test
    void Emitter_전체_조회() {
        ChatEmitterKey key1 = new ChatEmitterKey(4L, UUID.randomUUID());
        ChatEmitterKey key2 = new ChatEmitterKey(5L, UUID.randomUUID());
        SseEmitter emitter1 = new SseEmitter();
        SseEmitter emitter2 = new SseEmitter();

        repository.save(key1, emitter1);
        repository.save(key2, emitter2);

        Map<ChatEmitterKey, SseEmitter> allEmitters = repository.findAll();

        assertThat(allEmitters).containsEntry(key1, emitter1);
        assertThat(allEmitters).containsEntry(key2, emitter2);
        assertThat(allEmitters).hasSize(2);
    }
}

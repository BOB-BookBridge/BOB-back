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
  @DisplayName("Emitter 저장 및 조회 - 성공")
  void emitter를_저장하고_조회할_수_있다() {
    // given
    ChatEmitterKey key = new ChatEmitterKey(1L, UUID.randomUUID());
    SseEmitter emitter = new SseEmitter();

    // when
    repository.save(key, emitter);
    SseEmitter result = repository.get(key);

    // then
    assertThat(result).isEqualTo(emitter);
  }

  @Test
  @DisplayName("Emitter 존재 여부 확인 - 성공")
  void emitter의_존재_여부를_확인할_수_있다() {
    // given
    ChatEmitterKey key = new ChatEmitterKey(2L, UUID.randomUUID());
    SseEmitter emitter = new SseEmitter();
    repository.save(key, emitter);

    // when & then
    assertThat(repository.exists(key)).isTrue();
  }

  @Test
  @DisplayName("Emitter 삭제 - 성공")
  void emitter를_삭제_할_수_있다() {
    // given
    ChatEmitterKey key = new ChatEmitterKey(3L, UUID.randomUUID());
    SseEmitter emitter = new SseEmitter();
    repository.save(key, emitter);

    // when
    repository.remove(key);

    // then
    assertThat(repository.exists(key)).isFalse();
    assertThat(repository.get(key)).isNull();
  }

  @Test
  @DisplayName("모든 Emitter 조회 - 성공")
  void 모든_Emitter를_조회_할_수_있다() {
    // given
    ChatEmitterKey key1 = new ChatEmitterKey(4L, UUID.randomUUID());
    ChatEmitterKey key2 = new ChatEmitterKey(5L, UUID.randomUUID());
    SseEmitter emitter1 = new SseEmitter();
    SseEmitter emitter2 = new SseEmitter();

    repository.save(key1, emitter1);
    repository.save(key2, emitter2);

    // when
    Map<ChatEmitterKey, SseEmitter> allEmitters = repository.findAll();

    // then
    assertThat(allEmitters).containsEntry(key1, emitter1);
    assertThat(allEmitters).containsEntry(key2, emitter2);
    assertThat(allEmitters).hasSize(2);
  }
}
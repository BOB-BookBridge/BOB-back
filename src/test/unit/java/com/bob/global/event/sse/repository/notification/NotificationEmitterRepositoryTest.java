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
  @DisplayName("Emitter 저장 - 성공 테스트")
  void emitter를_저장할_수_있다() {
    // given
    String memberId = UUID.randomUUID().toString();
    SseEmitter emitter = new SseEmitter();

    // when
    repository.save(memberId, emitter);

    // then
    assertThat(repository.get(memberId)).isEqualTo(emitter);
  }

  @Test
  @DisplayName("Emitter 존재 여부 확인 - 성공 테스트")
  void emitter_존재여부를_확인할_수_있다() {
    // given
    String memberId = UUID.randomUUID().toString();
    SseEmitter emitter = new SseEmitter();
    repository.save(memberId, emitter);

    // when & then
    assertThat(repository.exists(memberId)).isTrue();
    assertThat(repository.exists("invalid-id")).isFalse();
  }

  @Test
  @DisplayName("Emitter 전체 조회 - 성공 테스트")
  void emitter_전체를_조회할_수_있다() {
    // given
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    repository.save(id1, new SseEmitter());
    repository.save(id2, new SseEmitter());

    // when
    Map<String, SseEmitter> allEmitters = repository.findAll();

    // then
    assertThat(allEmitters).hasSize(2)
        .containsKeys(id1, id2);
  }

  @Test
  @DisplayName("Emitter 삭제 - 성공 테스트")
  void emitter를_삭제할_수_있다() {
    // given
    String memberId = UUID.randomUUID().toString();
    repository.save(memberId, new SseEmitter());

    // when
    repository.remove(memberId);

    // then
    assertThat(repository.exists(memberId)).isFalse();
    assertThat(repository.get(memberId)).isNull();
  }
}
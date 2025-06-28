package com.bob.infra.sse.repository.notification;

import com.bob.infra.sse.repository.EmitterRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Component
public class NotificationEmitterRepository implements EmitterRepository<String> {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  @Override
  public SseEmitter save(String memberId, SseEmitter emitter) {
    emitters.put(memberId, emitter);
    return emitter;
  }

  @Override
  public SseEmitter get(String memberId) {
    return emitters.get(memberId);
  }

  @Override
  public Map<String, SseEmitter> findAll() {
    return emitters;
  }

  @Override
  public void remove(String memberId) {
    emitters.remove(memberId);
  }

  @Override
  public boolean exists(String memberId) {
    return emitters.containsKey(memberId);
  }
}

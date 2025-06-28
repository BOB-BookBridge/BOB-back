package com.bob.global.event.sse.repository.chat;

import com.bob.global.event.sse.repository.EmitterRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class ChatEmitterRepository implements EmitterRepository<ChatEmitterKey> {

  private final Map<ChatEmitterKey, SseEmitter> emitters = new ConcurrentHashMap<>();

  @Override
  public SseEmitter save(ChatEmitterKey key, SseEmitter emitter) {
    emitters.put(key, emitter);
    return emitter;
  }

  @Override
  public SseEmitter get(ChatEmitterKey key) {
    return emitters.get(key);
  }

  @Override
  public Map<ChatEmitterKey, SseEmitter> findAll() {
    return emitters;
  }

  @Override
  public void remove(ChatEmitterKey key) {
    emitters.remove(key);
  }

  @Override
  public boolean exists(ChatEmitterKey key) {
    return emitters.containsKey(key);
  }
}

package com.bob.global.event.sse.manager;

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Getter
@RequiredArgsConstructor
@Component
public class EmitterManager implements Runnable {

  private final EmitterRepository<String> notificationEmitterRepository;
  private final EmitterRepository<ChatEmitterKey> chatEmitterRepository;
  private final ScheduledExecutorService sseHeartbeatScheduler;

  @Value("${sse.default-timeout}")
  private Long defaultTimeout;

  @Value("${sse.heartbeat-interval}")
  private Long heartbeatInterval;

  @PostConstruct
  public void init() {
    sseHeartbeatScheduler.scheduleAtFixedRate(this, 10, heartbeatInterval, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    sendHeartbeat(notificationEmitterRepository.findAll(), notificationEmitterRepository);
    sendHeartbeat(chatEmitterRepository.findAll(), chatEmitterRepository);
  }

  private <T> void sendHeartbeat(Map<T, SseEmitter> emitters, EmitterRepository<T> repository) {
    emitters.forEach((key, emitter) -> sendEvent(key, emitter, "heartbeat", "ping", repository));
  }

  public <T> void sendEvent(T key, SseEmitter emitter, String eventName, Object data, EmitterRepository<T> repository) {
    try {
      emitter.send(SseEmitter.event().name(eventName).data(data));
    } catch (Exception e) {
      emitter.complete();
      repository.remove(key);
      log.warn("Failed to send SSE event - key: {}, event: {}, error: {}", key, eventName, e.toString());
    }
  }
}
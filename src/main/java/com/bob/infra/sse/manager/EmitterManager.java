package com.bob.infra.sse.manager;

import com.bob.infra.sse.repository.EmitterRepository;
import com.bob.infra.sse.repository.chat.ChatEmitterKey;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Component
public class EmitterManager implements Runnable {

  private final EmitterRepository<String> notificationEmitterRepository;
  private final EmitterRepository<ChatEmitterKey> chatEmitterRepository;
  private final ScheduledExecutorService sseHeartbeatScheduler;
  @Value("${sse.heartbeat-interval}")
  private Long heartbeatInterval;

  @PostConstruct
  public void init() {
    sseHeartbeatScheduler.scheduleAtFixedRate(this, 10, heartbeatInterval, TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    send(notificationEmitterRepository.findAll());
    send(chatEmitterRepository.findAll());
  }

  private <T> void send(Map<T, SseEmitter> emitters) {
    emitters.entrySet().removeIf(emitter -> {
      try {
        emitter.getValue().send(SseEmitter.event().name("heartbeat").data("ping"));
        return false;
      } catch (Exception e) {
        return true;
      }
    });
  }
}
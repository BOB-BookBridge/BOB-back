package com.bob.global.event.sse.manager;

import com.bob.global.event.sse.repository.EmitterRepository;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import com.bob.global.event.sse.repository.notification.NotiEmitterKey;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
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

  private final EmitterRepository<NotiEmitterKey> notificationEmitterRepository;
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

  public SseEmitter subscribeToChat(Long chatRoomId, UUID memberId) {
    ChatEmitterKey key = new ChatEmitterKey(chatRoomId, memberId);
    return subscribe(key, chatEmitterRepository, chatEmitterRepository::save);
  }

  public SseEmitter subscribeToNoti(UUID memberId) {
    NotiEmitterKey key = new NotiEmitterKey(memberId);
    return subscribe(key, notificationEmitterRepository, notificationEmitterRepository::save);
  }

  private <T> SseEmitter subscribe(T key, EmitterRepository<T> repository, BiConsumer<T, SseEmitter> saveProcess) {
    verifyDuplicateEmitter(key, repository);
    SseEmitter emitter = new SseEmitter(defaultTimeout);
    setupEmitter(emitter, key, repository);
    saveProcess.accept(key, emitter);
    sendEvent(key, emitter, "connect", "connected", repository);
    return emitter;
  }

  private <T> void verifyDuplicateEmitter(T key, EmitterRepository<T> repository) {
    SseEmitter existingEmitter = repository.get(key);
    if (existingEmitter != null) {
      existingEmitter.complete();
      repository.remove(key);
    }
  }

  private <T> void setupEmitter(SseEmitter emitter, T key, EmitterRepository<T> repository) {
    emitter.onCompletion(() -> repository.remove(key));
    emitter.onTimeout(() -> repository.remove(key));
    emitter.onError((e) -> repository.remove(key));
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
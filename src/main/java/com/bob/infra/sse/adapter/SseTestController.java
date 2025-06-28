package com.bob.infra.sse.adapter;

import com.bob.infra.sse.manager.EmitterManager;
import com.bob.infra.sse.repository.EmitterRepository;
import com.bob.web.common.AuthenticationId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class SseTestController {

  private final EmitterRepository<String> notificationEmitterRepository;
  private final EmitterManager emitterManager;

  @GetMapping("/sse/test/subscribe")
  public SseEmitter subscribe(@AuthenticationId UUID memberId) {
    SseEmitter emitter = setupEmitter(memberId.toString());
    sendConnectEvent(memberId.toString(), emitter);
    return emitter;
  }

  private SseEmitter setupEmitter(String participantId) {
    SseEmitter emitter = new SseEmitter(emitterManager.getDefaultTimeout());
    emitter.onCompletion(() -> notificationEmitterRepository.remove(participantId));
    emitter.onTimeout(() -> notificationEmitterRepository.remove(participantId));
    emitter.onError(e -> notificationEmitterRepository.remove(participantId));
    notificationEmitterRepository.save(participantId, emitter);
    return emitter;
  }

  private void sendConnectEvent(String memberId, SseEmitter emitter) {
    emitterManager.sendEvent(memberId, emitter, "connect", "connected", notificationEmitterRepository);
  }
}

package com.bob.global.event.sse.presentation;

import static com.bob.global.utils.web.CookieUtils.getCookie;

import com.bob.infra.auth.jwt.JwtProvider;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.repository.EmitterRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class SseTestController {

  private final EmitterRepository<String> notificationEmitterRepository;
  private final EmitterManager emitterManager;

  private final JwtProvider jwtProvider;

  @GetMapping("/sse/test/subscribe")
  public SseEmitter subscribe(HttpServletRequest request) {
    String token = getCookie(request, "AUTHORIZATION");
    String memberId = jwtProvider.getMemberId(token).toString();
    SseEmitter emitter = setupEmitter(memberId);
    sendConnectEvent(emitter, memberId);
    return emitter;
  }

  private SseEmitter setupEmitter(String key) {
    SseEmitter emitter = new SseEmitter(emitterManager.getDefaultTimeout());
    emitter.onCompletion(() -> notificationEmitterRepository.remove(key));
    emitter.onTimeout(() -> notificationEmitterRepository.remove(key));
    emitter.onError((e) -> notificationEmitterRepository.remove(key));
    notificationEmitterRepository.save(key, emitter);
    return emitter;
  }

  private void sendConnectEvent(SseEmitter emitter, String key) {
    emitterManager.sendEvent(key, emitter, "connect", "connected", notificationEmitterRepository);
  }
}

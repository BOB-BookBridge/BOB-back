package com.bob.web.notification.controller;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.web.common.AuthenticationId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotiStreamController {

  private final EmitterManager emitterManager;

  @GetMapping("/subscribe")
  public SseEmitter handleSubscribeNotification(@AuthenticationId UUID memberId) {
    return emitterManager.subscribeToNoti(memberId);
  }
}

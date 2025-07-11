package com.bob.web.notification.contoller;

import static com.bob.global.utils.web.CookieUtils.getCookie;

import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.infra.auth.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
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
  private final JwtProvider jwtProvider;

  @GetMapping("/subscribe")
  public SseEmitter handleSubscribeNotification(HttpServletRequest request) {
    String token = getCookie(request, "AUTHORIZATION");
    verifyAuthenticationRequest(token);
    return emitterManager.subscribeToNoti(jwtProvider.getMemberId(token));
  }

  private void verifyAuthenticationRequest(String token) {
    if (token == null || !jwtProvider.isVerified(token)) {
      throw new ApplicationException(ApplicationError.AUTHENTICATION_FAILED);
    }
  }
}

package com.bob.web.chat.controller;

import static com.bob.global.utils.web.CookieUtils.getCookie;

import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ValidateParticipantQuery;
import com.bob.domain.chat.usecase.ChatRoomModifyUseCase;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import com.bob.infra.auth.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/chatrooms")
@RestController
public class ChatStreamController {

  private final ChatRoomReadUseCase readUseCase;
  private final ChatRoomModifyUseCase modifyUseCase;

  private final EmitterManager emitterManager;
  private final JwtProvider jwtProvider;

  @GetMapping("/{chatRoomId}/subscribe")
  public SseEmitter handleSubscribeChat(@PathVariable Long chatRoomId, HttpServletRequest request) {
    String token = getCookie(request, "AUTHORIZATION");
    verifyAuthenticationRequest(token);

    UUID memberId = jwtProvider.getMemberId(token);
    readUseCase.validateParticipant(ValidateParticipantQuery.of(chatRoomId, memberId));
    modifyUseCase.enterChatRoomProcess(EnterChatRoomCommand.of(chatRoomId, memberId));
    return emitterManager.subscribeToChat(chatRoomId, memberId);
  }

  private void verifyAuthenticationRequest(String token) {
    if (token == null || !jwtProvider.isVerified(token)) {
      throw new ApplicationException(ApplicationError.AUTHENTICATION_FAILED);
    }
  }
}

package com.bob.web.chat.controller;

import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.dto.query.ValidateParticipantQuery;
import com.bob.domain.chat.usecase.ChatRoomModifyUseCase;
import com.bob.domain.chat.usecase.ChatRoomReadUseCase;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.web.common.AuthenticationId;
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

  @GetMapping("/{chatRoomId}/subscribe")
  public SseEmitter handleSubscribeChat(@PathVariable Long chatRoomId, @AuthenticationId UUID memberId) {
    readUseCase.validateParticipant(ValidateParticipantQuery.of(chatRoomId, memberId));
    modifyUseCase.enterChatRoomProcess(EnterChatRoomCommand.of(chatRoomId, memberId));
    return emitterManager.subscribeToChat(chatRoomId, memberId);
  }
}

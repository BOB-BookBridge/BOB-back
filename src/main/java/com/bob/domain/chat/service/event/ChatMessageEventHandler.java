package com.bob.domain.chat.service.event;

import static com.bob.domain.chat.service.dto.command.CreateSystemMessageCommand.of;

import com.bob.domain.chat.service.ChatRoomService;
import com.bob.domain.chat.service.dto.command.CreateSystemMessageCommand;
import com.bob.global.event.application.dto.SystemChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageEventHandler {

  private final ChatRoomService chatRoomService;

  @EventListener
  public void handleSystemChatMessageEvent(SystemChatMessageEvent event) {
    CreateSystemMessageCommand command = of(
        event.domain(), event.refId(), event.memberId(), event.partnerId(), event.body()
    );
    chatRoomService.createChatRoomSystemMessageProcess(command);
  }
}

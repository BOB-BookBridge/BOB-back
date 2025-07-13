package com.bob.domain.chat.service;

import static com.bob.global.event.sse.manager.type.EmitterType.CHAT;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.repository.ChatMessageRepository;
import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.dto.command.EnterChatRoomCommand;
import com.bob.domain.chat.service.port.out.ChatFilePort;
import com.bob.global.event.sse.manager.EmitterManager;
import com.bob.global.event.sse.repository.chat.ChatEmitterKey;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final EmitterManager emitterManager;
  private final ChatFilePort filePort;

  @Transactional
  public ChatMessage createChatMessageProcess(CreateChatMessageCommand command, UUID partnerId) {
    ChatMessage message = chatMessageRepository.save(command.toChatMessage());
    if (emitterManager.isExistClientConnection(CHAT, ChatEmitterKey.of(command.chatRoomId(), partnerId))) {
      message.readMessage();
    }
    imageMapping(command.fileNames(), message.getId());
    return message;
  }

  private void imageMapping(List<String> fileNames, Long messageId) {
    if (fileNames == null || fileNames.isEmpty()) {
      return;
    }
    filePort.modifyReferenceId(fileNames, String.valueOf(messageId));
  }

  @Transactional
  public void updateReadStatusProcess(EnterChatRoomCommand command) {
    List<ChatMessage> messages = chatMessageRepository.findUnreadMessages(command.chatRoomId(), command.memberId());
    for (ChatMessage message : messages) {
      message.readMessage();
    }
  }
}

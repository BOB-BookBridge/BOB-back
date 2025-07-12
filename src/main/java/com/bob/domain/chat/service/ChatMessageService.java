package com.bob.domain.chat.service;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.repository.ChatMessageRepository;
import com.bob.domain.chat.service.dto.command.CreateChatMessageCommand;
import com.bob.domain.chat.service.port.out.ChatFilePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;
  private final ChatFilePort filePort;

  @Transactional
  public ChatMessage createChatMessage(CreateChatMessageCommand command) {
    ChatMessage message = chatMessageRepository.save(command.toChatMessage());
    imageMapping(command.fileNames(), message.getId());
    return message;
  }

  private void imageMapping(List<String> fileNames, Long messageId) {
    if (fileNames == null || fileNames.isEmpty()) {
      return;
    }
    filePort.modifyReferenceId(fileNames, String.valueOf(messageId));
  }
}

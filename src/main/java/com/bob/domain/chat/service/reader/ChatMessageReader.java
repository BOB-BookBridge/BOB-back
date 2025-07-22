package com.bob.domain.chat.service.reader;

import com.bob.domain.chat.entity.ChatMessage;
import com.bob.domain.chat.repository.ChatMessageRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatMessageReader {

  private final ChatMessageRepository chatMessageRepository;

  public List<ChatMessage> readMessagesOfChatRoom(Long chatRoomId, LocalDateTime enteredAt) {
    return chatMessageRepository.findAllByChatRoomIDAfterEnteredAt(chatRoomId, enteredAt);
  }

  public int readUnreadMessageCountOfChatRoom(Long chatRoomId, UUID receiverId) {
    return chatMessageRepository.countUnreadMessage(chatRoomId, receiverId);
  }
}

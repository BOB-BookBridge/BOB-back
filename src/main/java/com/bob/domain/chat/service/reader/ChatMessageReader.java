package com.bob.domain.chat.service.reader;

import com.bob.domain.chat.repository.ChatMessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatMessageReader {

  private final ChatMessageRepository chatRoomMessageRepository;

  public int readUnreadMessageCountOfChatRoom(Long chatRoomId, UUID receiverId) {
    return chatRoomMessageRepository.countUnreadMessage(chatRoomId, receiverId);
  }
}

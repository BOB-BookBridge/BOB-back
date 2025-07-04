package com.bob.domain.chat.service.reader;

import com.bob.domain.chat.entity.ChatRoom;
import com.bob.domain.chat.repository.ChatRoomRepository;
import com.bob.global.exception.exceptions.ApplicationException;
import com.bob.global.exception.response.ApplicationError;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatRoomReader {

  private final ChatRoomRepository chatRoomRepository;

  public ChatRoom readChatRoomById(Long chatRoomId) {
    return chatRoomRepository.findById(chatRoomId)
        .orElseThrow(() -> new ApplicationException(ApplicationError.NOT_EXISTS_CHAT_ROOM));
  }

  public List<ChatRoom> readParticipatingChatRoomsByMemberId(UUID memberId) {
    return chatRoomRepository.findAllByMemberId(memberId);
  }

  public Optional<Long> readExistingChatRoom(Long postId, UUID sellerId, UUID buyerId) {
    return chatRoomRepository.findExistingChatRoom(postId, sellerId, buyerId);
  }
}

package com.bob.domain.chat.repository;

import com.bob.domain.chat.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

  List<ChatMessage> findAllByChatRoomId(Long chatRoomId);

  @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.chatRoomId = :chatRoomId
          AND m.createdAt >= :enteredAt
        ORDER BY m.id DESC
      """)
  List<ChatMessage> findRecentMessages(Long chatRoomId, LocalDateTime enteredAt, Pageable pageable);

  @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.chatRoomId = :chatRoomId
          AND m.id < :beforeMessageId
          AND m.createdAt >= :enteredAt
        ORDER BY m.id DESC
      """)
  List<ChatMessage> findMessagesBeforeId(Long chatRoomId, Long beforeMessageId, LocalDateTime enteredAt, Pageable pageable);

  @Query("""
        SELECT cm
        FROM ChatMessage cm
        WHERE cm.chatRoomId = :chatRoomId
          AND cm.senderId <> :receiverId
          AND cm.isRead = false
      """)
  List<ChatMessage> findUnreadMessages(Long chatRoomId, UUID receiverId);

  @Query("""
        SELECT COUNT(cm)
        FROM ChatMessage cm
        WHERE cm.chatRoomId = :chatRoomId
          AND cm.senderId <> :senderId
          AND cm.isRead = false
      """)
  int countUnreadMessage(Long chatRoomId, UUID senderId);
}

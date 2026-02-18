package com.bob.core.chat.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bob.core.chat.domain.ChatMessage;
import com.bob.core.chat.domain.Chatroom;
import com.bob.core.chat.domain.repository.projection.ChatroomUnreadCount;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
          SELECT cr
          FROM Chatroom cr
            JOIN cr.members crm
          WHERE crm.memberId = :memberId
            AND crm.exitedAt IS NULL
        """)
    List<Chatroom> findAllByMemberId(UUID memberId);

    @Query("""
          SELECT cr
          FROM Chatroom cr
            JOIN cr.members crm
          WHERE cr.postId = :postId
            AND crm.memberId = :buyerId
        """)
    Optional<Chatroom> findByPostAndBuyer(Long postId, UUID buyerId);

    @Query("""
          SELECT cr
          FROM Chatroom cr
            JOIN cr.messages m
          WHERE m.id = :messageId
        """)
    Optional<Chatroom> findByMessageId(Long messageId);

    @Query("""
          SELECT m
          FROM Chatroom cr
            JOIN cr.messages m
          WHERE cr.id = :chatroomId
            AND m.createdAt >= :enteredAt
          ORDER BY m.id ASC
        """)
    List<ChatMessage> findAfterEnteredAtMessages(Long chatroomId, LocalDateTime enteredAt);

    @Query("""
          SELECT COUNT(m)
          FROM Chatroom cr
            JOIN cr.members cm
            JOIN cr.messages m
          WHERE cm.memberId = :memberId
            AND cm.exitedAt IS NULL
            AND m.senderId <> :memberId
            AND m.type <> 'SYSTEM'
            AND (cm.lastReadMessageId IS NULL OR m.id > cm.lastReadMessageId)
        """)
    Long countUnreadMessagesByMember(UUID memberId);

    @Query("""
          SELECT cr.id AS chatroomId, COUNT(m) AS unreadCount
          FROM Chatroom cr
            JOIN cr.members cm
            JOIN cr.messages m
          WHERE cm.memberId = :memberId
            AND cm.exitedAt IS NULL
            AND m.senderId <> :memberId
            AND m.type <> 'SYSTEM'
            AND (cm.lastReadMessageId IS NULL OR m.id > cm.lastReadMessageId)
          GROUP BY cr.id
        """)
    List<ChatroomUnreadCount> countAllUnreadMessages(UUID memberId);
}

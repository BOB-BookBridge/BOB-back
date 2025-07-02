package com.bob.domain.chat.repository;

import com.bob.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

  @Query("""
        SELECT cr
        FROM ChatRoom cr
          JOIN ChatRoomMember crm ON cr.id = crm.chatRoomId
        WHERE crm.memberId = :memberId
      """)
  List<ChatRoom> findAllByMemberId(UUID memberId);

  @Query("""
        SELECT crm.chatRoomId
        FROM ChatRoomMember crm
        WHERE crm.chatRoomId IN (
            SELECT cr.id
            FROM ChatRoom cr
            WHERE cr.postId = :postId
        )
        AND crm.memberId IN (:sellerId, :buyerId)
        GROUP BY crm.chatRoomId
        HAVING COUNT(DISTINCT crm.memberId) = 2
      """)
  Optional<Long> findExistingChatRoom(Long postId, UUID sellerId, UUID buyerId);
}

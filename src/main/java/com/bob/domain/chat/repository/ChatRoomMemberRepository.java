package com.bob.domain.chat.repository;

import com.bob.domain.chat.entity.ChatRoomMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ChatRoomMemberRepository extends CrudRepository<ChatRoomMember, Long> {

  Optional<ChatRoomMember> findByChatRoomIdAndMemberId(Long chatRoomId, UUID memberId);

  @Query("""
        SELECT crm.memberId
        FROM ChatRoomMember crm
        WHERE crm.chatRoomId = :chatRoomId
          AND crm.memberId <> :myId
      """)
  Optional<UUID> findPartnerIdByRequesterId(Long chatRoomId, UUID myId);

  List<ChatRoomMember> findByChatRoomId(Long chatRoomId);
}
